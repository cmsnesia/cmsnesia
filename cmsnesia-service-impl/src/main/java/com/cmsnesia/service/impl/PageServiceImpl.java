package com.cmsnesia.service.impl;

import com.cmsnesia.assembler.PageAssembler;
import com.cmsnesia.domain.Page;
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

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PageServiceImpl implements PageService {

  private final PageAssembler pageAssembler;
  private final PageRepo pageRepo;

  @Override
  public Mono<Result<PageDto>> add(AuthDto session, PageDto dto) {
    com.cmsnesia.domain.Page page = pageAssembler.fromDto(dto);
    page.setId(UUID.randomUUID().toString());
    page.setCreatedBy(session.getId());
    page.setCreatedAt(new Date());
    page.setApplications(Sessions.applications(session));
    return pageRepo
        .save(page)
        .map(pageAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
  }

  @Override
  public Mono<Result<PageDto>> edit(AuthDto session, PageDto dto) {
    return pageRepo
        .findById(dto.getId())
        .flatMap(
            page -> {
              Page save = pageAssembler.fromDto(dto);
              save.audit(page);
              return pageRepo
                  .save(save)
                  .map(
                      result ->
                          Result.build(pageAssembler.fromEntity(result), StatusCode.SAVE_SUCCESS));
            });
  }

  @Override
  public Mono<Result<PageDto>> delete(AuthDto session, PageDto dto) {
    return pageRepo
        .findById(dto.getId())
        .flatMap(
            page -> {
              Page save = pageAssembler.fromDto(dto);
              save.audit(page);
              save.setDeletedAt(new Date());
              save.setDeletedBy(session.getId());
              return pageRepo.save(save);
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

  @Override
  public Mono<Result<PageDto>> findAbout(AuthDto session) {
    return pageRepo
        .findAbout(session)
        .map(pageAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }
}
