package com.cmsnesia.service.impl;

import com.cmsnesia.assembler.PageAssembler;
import com.cmsnesia.domain.Page;
import com.cmsnesia.domain.model.Author;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.PageDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.PageService;
import com.cmsnesia.service.repository.PageRepo;
import com.cmsnesia.service.util.Sessions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PageServiceImpl implements PageService {

  private final PageAssembler pageAssembler;
  private final PageRepo pageRepo;

  @Override
  public Mono<Result<PageDto>> add(AuthDto session, PageDto dto) {
    return pageRepo
        .exists(session, null, dto.getName())
        .flatMap(
            exists -> {
              if (!exists) {
                com.cmsnesia.domain.Page page = pageAssembler.fromDto(dto);
                page.setId(UUID.randomUUID().toString());
                page.setCreatedBy(session.getId());
                page.setCreatedAt(new Date());
                page.setAuthors(
                    Arrays.asList(
                            Author.builder()
                                .name(session.getFullName())
                                .modifiedAt(new Date())
                                .build())
                        .stream()
                        .collect(Collectors.toSet()));
                page.setApplications(Sessions.applications(session));
                return pageRepo
                    .save(page)
                    .map(pageAssembler::fromEntity)
                    .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }

  @Override
  public Mono<Result<PageDto>> edit(AuthDto session, PageDto dto) {
    return pageRepo
        .exists(session, dto.getId(), dto.getName())
        .flatMap(
            exists -> {
              if (!exists) {
                return pageRepo
                    .findById(dto.getId())
                    .flatMap(
                        page -> {
                          Page save = pageAssembler.fromDto(dto);
                          save.audit(page);
                          Set<Author> authors =
                              page.getAuthors() == null ? new HashSet<>() : page.getAuthors();
                          if (authors.stream()
                              .map(Author::getName)
                              .noneMatch(name -> name.equals(session.getFullName()))) {
                            Author author = new Author();
                            author.setName(session.getFullName());
                            author.setModifiedAt(new Date());
                            authors.add(author);
                          }
                          save.setAuthors(authors);
                          return pageRepo
                              .save(save)
                              .map(
                                  result ->
                                      Result.build(
                                          pageAssembler.fromEntity(result),
                                          StatusCode.SAVE_SUCCESS));
                        });
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }

  @Override
  public Mono<Result<PageDto>> delete(AuthDto session, PageDto dto) {
    return pageRepo
        .findById(dto.getId())
        .flatMap(
            page -> {
              page.setDeletedAt(new Date());
              page.setDeletedBy(session.getId());
              return pageRepo.save(page);
            })
        .map(pageAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.DELETE_SUCCESS));
  }

  @Override
  public Mono<org.springframework.data.domain.Page<PageDto>> find(
      AuthDto session, PageDto dto, Pageable pageable) {
    return pageRepo
        .countFind(session, dto)
        .flatMap(
            count -> {
              Mono<List<PageDto>> mono =
                  pageRepo
                      .find(session, dto, pageable)
                      .map(page -> pageAssembler.fromEntity(page))
                      .collectList();
              return mono.map(pageDtos -> new PageImpl<>(pageDtos, pageable, count));
            });
  }

  @Override
  public Mono<Result<PageDto>> find(AuthDto session, IdRequest idRequest) {
    return pageRepo
        .findById(idRequest.getId())
        .map(page -> pageAssembler.fromEntity(page))
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }
}
