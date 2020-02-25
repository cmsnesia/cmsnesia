package com.cmsnesia.service.impl;

import com.cmsnesia.assembler.CategoryAssembler;
import com.cmsnesia.domain.Category;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.CategoryService;
import com.cmsnesia.service.repository.CategoryRepo;
import com.cmsnesia.service.repository.PostRepo;
import com.cmsnesia.service.util.Sessions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

  private final CategoryAssembler categoryAssembler;
  private final CategoryRepo categoryRepo;
  private final PostRepo postRepo;

  @Override
  public Mono<Result<CategoryDto>> add(AuthDto session, CategoryDto dto) {
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
  public Mono<Result<CategoryDto>> edit(AuthDto session, CategoryDto dto) {
    return categoryRepo
        .exists(session, dto.getId(), dto.getName())
        .flatMap(
            exists -> {
              if (!exists) {
                return categoryRepo
                    .findById(dto.getId())
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
  public Mono<Result<CategoryDto>> delete(AuthDto authDto, CategoryDto dto) {
    return categoryRepo
        .findById(dto.getId())
        .flatMap(
            (Function<Category, Mono<Result<CategoryDto>>>)
                category -> {
                  category.setDeletedBy(authDto.getId());
                  category.setDeletedAt(new Date());
                  return categoryRepo
                      .save(category)
                      .map(saved -> categoryAssembler.fromEntity(saved))
                      .map(result -> Result.build(result, StatusCode.DELETE_SUCCESS));
                });
  }

  @Override
  public Mono<Page<CategoryDto>> find(AuthDto authDto, CategoryDto dto, Pageable pageable) {
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
  public Mono<Result<CategoryDto>> find(AuthDto session, IdRequest idRequest) {
    return categoryRepo
        .find(session, idRequest)
        .map(categoryAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }

  @Override
  public Mono<Result<CategoryDto>> findById(AuthDto session, IdRequest id) {
    return categoryRepo
        .findById(id.getId())
        .map(category -> categoryAssembler.fromEntity(category))
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }

  @Override
  public Mono<Set<CategoryDto>> findByIds(AuthDto session, Set<IdRequest> ids) {
    return categoryRepo
        .findAllById(ids.stream().map(IdRequest::getId).collect(Collectors.toSet()))
        .collectList()
        .map(categoryAssembler::fromEntity);
  }

  @Override
  public Mono<Result<Boolean>> exists(AuthDto session, Set<IdRequest> ids) {
    return categoryRepo
        .exists(session, ids.stream().map(IdRequest::getId).collect(Collectors.toSet()))
        .map(
            result ->
                Result.build(result, result ? StatusCode.DATA_FOUND : StatusCode.DATA_NOT_FOUND));
  }
}
