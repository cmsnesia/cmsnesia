package com.cmsnesia.service.impl;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.CategoryGroupAssembler;
import com.cmsnesia.domain.CategoryGroup;
import com.cmsnesia.model.CategoryGroupDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.CategoryGroupService;
import com.cmsnesia.domain.repository.CategoryGroupRepo;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CategoryGroupServiceImpl implements CategoryGroupService {

  private final CategoryGroupAssembler categoryGroupAssembler;
  private final CategoryGroupRepo categoryGroupRepo;

  @Override
  public Mono<Result<CategoryGroupDto>> add(Session session, CategoryGroupDto dto) {
    return categoryGroupRepo
        .exists(session, null, dto.getName())
        .flatMap(
            exists -> {
              if (!exists) {
                CategoryGroup categoryGroup = categoryGroupAssembler.fromDto(dto);
                categoryGroup.setId(UUID.randomUUID().toString());
                categoryGroup.setCreatedBy(session.getId());
                categoryGroup.setCreatedAt(new Date());
                categoryGroup.setApplications(Session.applications(session));
                return categoryGroupRepo
                    .save(categoryGroup)
                    .map(categoryGroupAssembler::fromEntity)
                    .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }

  @Override
  public Mono<Result<CategoryGroupDto>> edit(Session session, CategoryGroupDto dto) {
    return categoryGroupRepo
        .exists(session, dto.getId(), dto.getName())
        .flatMap(
            exists -> {
              if (!exists) {
                return categoryGroupRepo
                    .find(session, dto.getId())
                    .flatMap(
                        categoryGroup -> {
                          CategoryGroup save = categoryGroupAssembler.fromDto(dto);
                          save.audit(categoryGroup);
                          save.setModifiedBy(session.getId());
                          save.setModifiedAt(new Date());
                          return categoryGroupRepo
                              .save(save)
                              .map(categoryGroupAssembler::fromEntity)
                              .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
                        });
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }

  @Override
  public Mono<Result<CategoryGroupDto>> delete(Session session, CategoryGroupDto dto) {
    return categoryGroupRepo
        .exists(session, new HashSet<>(Arrays.asList(dto.getId())))
        .flatMap(
            exists -> {
              if (exists) {
                return categoryGroupRepo
                    .find(session, dto.getId())
                    .flatMap(
                        categoryGroup ->
                            categoryGroupRepo
                                .deleteById(categoryGroup.getId())
                                .map(aVoid -> Result.build(dto, StatusCode.DELETE_SUCCESS)));
              } else {
                return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
              }
            });
  }

  @Override
  public Mono<Page<CategoryGroupDto>> find(
      Session session, CategoryGroupDto dto, Pageable pageable) {
    return categoryGroupRepo
        .countFind(session, dto)
        .flatMap(
            count -> {
              Mono<List<CategoryGroupDto>> mono =
                  categoryGroupRepo
                      .find(session, dto, pageable)
                      .map(categoryGroup -> categoryGroupAssembler.fromEntity(categoryGroup))
                      .collectList();
              return mono.map(
                  categoryGroupDtos ->
                      new PageImpl<CategoryGroupDto>(categoryGroupDtos, pageable, count));
            });
  }

  @Override
  public Mono<Result<CategoryGroupDto>> find(Session session, IdRequest idRequest) {
    return categoryGroupRepo
        .find(session, idRequest.getId())
        .map(categoryGroupAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }
}
