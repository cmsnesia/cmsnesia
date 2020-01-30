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
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CategoryServiceImpl implements CategoryService {

  private final CategoryAssembler categoryAssembler;
  private final CategoryRepo categoryRepo;
  private final PostRepo postRepo;

  public CategoryServiceImpl(
      CategoryAssembler categoryAssembler, CategoryRepo categoryRepo, PostRepo postRepo) {
    this.categoryAssembler = categoryAssembler;
    this.categoryRepo = categoryRepo;
    this.postRepo = postRepo;
  }

  @Override
  public Mono<Result<CategoryDto>> add(AuthDto authDto, CategoryDto dto) {
    Category category = categoryAssembler.fromDto(dto);
    category.setId(UUID.randomUUID().toString());
    category.setCreatedBy(authDto.getId());
    category.setCreatedAt(new Date());
    return categoryRepo
        .save(category)
        .map(categoryAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
  }

  @Override
  public Mono<Result<CategoryDto>> edit(AuthDto authDto, CategoryDto dto) {
    return categoryRepo
        .findById(dto.getId())
        .flatMap(
            (Function<Category, Mono<Result<CategoryDto>>>)
                category -> {
                  Category save = categoryAssembler.fromDto(dto);
                  save.audit(category);
                  save.setModifiedBy(authDto.getId());
                  save.setModifiedAt(new Date());
                  postRepo
                      .findAndModifyCategory(authDto, categoryAssembler.fromEntity(category))
                      .block(); // blocking part
                  return categoryRepo
                      .save(save)
                      .map(saved -> categoryAssembler.fromEntity(saved))
                      .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
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
  public Mono<Result<CategoryDto>> findById(AuthDto session, String id) {
    return categoryRepo
        .findById(id)
        .map(category -> categoryAssembler.fromEntity(category))
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }

  @Override
  public Mono<Result<Boolean>> exists(AuthDto session, Set<String> ids) {
    return categoryRepo
        .exists(ids)
        .map(
            result ->
                Result.build(result, result ? StatusCode.DATA_FOUND : StatusCode.DATA_NOT_FOUND));
  }
}
