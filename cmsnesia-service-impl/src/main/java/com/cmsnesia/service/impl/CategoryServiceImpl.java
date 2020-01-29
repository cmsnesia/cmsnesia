package com.cmsnesia.service.impl;

import com.cmsnesia.assembler.CategoryAssembler;
import com.cmsnesia.domain.Category;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.service.CategoryService;
import com.cmsnesia.service.repository.CategoryRepo;
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

  public CategoryServiceImpl(CategoryAssembler categoryAssembler, CategoryRepo categoryRepo) {
    this.categoryAssembler = categoryAssembler;
    this.categoryRepo = categoryRepo;
  }

  @Override
  public Mono<CategoryDto> add(AuthDto authDto, CategoryDto dto) {
    Category category = categoryAssembler.fromDto(dto);
    category.setId(UUID.randomUUID().toString());
    category.setCreatedBy(authDto.getId());
    category.setCreatedAt(new Date());
    return categoryRepo.save(category).map(categoryAssembler::fromEntity);
  }

  @Override
  public Mono<CategoryDto> edit(AuthDto authDto, CategoryDto dto) {
    return categoryRepo
        .findById(dto.getId())
        .flatMap(
            (Function<Category, Mono<CategoryDto>>)
                category -> {
                  Category save = categoryAssembler.fromDto(dto);
                  save.audit(category);
                  save.setModifiedBy(authDto.getId());
                  save.setModifiedAt(new Date());
                  return categoryRepo
                      .save(save)
                      .map(result -> categoryAssembler.fromEntity(result));
                });
  }

  @Override
  public Mono<CategoryDto> delete(AuthDto authDto, CategoryDto dto) {
    return categoryRepo
        .findById(dto.getId())
        .flatMap(
            (Function<Category, Mono<CategoryDto>>)
                category -> {
                  category.setDeletedBy(authDto.getId());
                  category.setDeletedAt(new Date());
                  return categoryRepo
                      .save(category)
                      .map(result -> categoryAssembler.fromEntity(result));
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
  public Mono<CategoryDto> findById(AuthDto session, String id) {
    return categoryRepo.findById(id).map(category -> categoryAssembler.fromEntity(category));
  }

  @Override
  public Mono<Boolean> exists(AuthDto session, Set<String> ids) {
    return categoryRepo.exists(ids);
  }
}
