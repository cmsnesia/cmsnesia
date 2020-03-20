package com.cmsnesia.service.impl;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.CategoryAssembler;
import com.cmsnesia.domain.Category;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.CategoryService;
import com.cmsnesia.service.repository.CategoryRepo;
import com.cmsnesia.service.repository.PostRepo;
import com.cmsnesia.service.util.Sessions;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

  private final CategoryAssembler categoryAssembler;
  private final CategoryRepo categoryRepo;
  private final PostRepo postRepo;

  @Override
  public Mono<Result<CategoryDto>> add(Session session, CategoryDto dto) {
    return categoryRepo
        .exists(session, null, dto.getName())
        .flatMap(
            exists -> {
              if (!exists) {
                Category category = categoryAssembler.fromDto(dto);
                category.setId(UUID.randomUUID().toString());
                category.setCreatedBy(session.getId());
                category.setCreatedAt(new Date());
                category.setApplications(Sessions.applications(session));
                return categoryRepo
                    .save(category)
                    .map(categoryAssembler::fromEntity)
                    .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }

  @Transactional
  @Override
  public Mono<Result<CategoryDto>> edit(Session session, CategoryDto dto) {
    return categoryRepo
        .exists(session, dto.getId(), dto.getName())
        .flatMap(
            exists -> {
              if (!exists) {
                return categoryRepo
                    .find(session, IdRequest.builder().id(dto.getId()).build())
                    .flatMap(
                        (Function<Category, Mono<Result<CategoryDto>>>)
                            category -> {
                              Category save = categoryAssembler.fromDto(dto);
                              save.audit(category);
                              save.setModifiedBy(session.getId());
                              save.setModifiedAt(new Date());
                              return postRepo
                                  .findAndModifyCategory(session, dto)
                                  .flatMap(
                                      updateResult -> {
                                        return categoryRepo
                                            .save(save)
                                            .map(saved -> categoryAssembler.fromEntity(saved))
                                            .map(
                                                result ->
                                                    Result.build(result, StatusCode.SAVE_SUCCESS));
                                      });
                            });
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }

  @Override
  public Mono<Result<CategoryDto>> delete(Session session, CategoryDto dto) {
    return categoryRepo
        .exists(session, new HashSet<>(Arrays.asList(dto.getId())))
        .flatMap(
            exists -> {
              if (exists) {
                return categoryRepo
                    .find(session, IdRequest.builder().id(dto.getId()).build())
                    .flatMap(
                        (Function<Category, Mono<Result<CategoryDto>>>)
                            category -> {
                              category.setDeletedBy(session.getId());
                              category.setDeletedAt(new Date());
                              return categoryRepo
                                  .save(category)
                                  .map(saved -> categoryAssembler.fromEntity(saved))
                                  .map(result -> Result.build(result, StatusCode.DELETE_SUCCESS));
                            });
              } else {
                return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
              }
            });
  }

  @Override
  public Mono<Page<CategoryDto>> find(Session authDto, CategoryDto dto, Pageable pageable) {
    return categoryRepo
        .countFind(authDto, dto)
        .flatMap(
            count -> {
              Mono<List<CategoryDto>> mono =
                  categoryRepo
                      .find(authDto, dto, pageable)
                      .map(category -> categoryAssembler.fromEntity(category))
                      .collectList();
              return mono.map(
                  categoryDtos -> new PageImpl<CategoryDto>(categoryDtos, pageable, count));
            });
  }

  @Override
  public Mono<Result<CategoryDto>> find(Session session, IdRequest idRequest) {
    return categoryRepo
        .find(session, idRequest)
        .map(categoryAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }

  @Override
  public Mono<Set<CategoryDto>> findByIds(Session session, Set<IdRequest> ids) {
    return categoryRepo.find(session, ids).collectList().map(categoryAssembler::fromEntity);
  }

  @Override
  public Mono<Result<Boolean>> exists(Session session, Set<IdRequest> ids) {
    return categoryRepo
        .exists(session, ids.stream().map(IdRequest::getId).collect(Collectors.toSet()))
        .map(
            result ->
                Result.build(result, result ? StatusCode.DATA_FOUND : StatusCode.DATA_NOT_FOUND));
  }
}
