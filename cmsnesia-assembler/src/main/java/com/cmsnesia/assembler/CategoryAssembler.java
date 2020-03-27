package com.cmsnesia.assembler;

import com.cmsnesia.domain.Category;
import com.cmsnesia.model.CategoryDto;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class CategoryAssembler extends Assembler<Category, CategoryDto> {

  @Nonnull
  @Override
  public Category fromDto(@Nonnull CategoryDto dto) {
    return Category.builder().id(dto.getId()).name(dto.getName()).link(dto.getLink()).build();
  }

  @Nonnull
  @Override
  public Set<Category> fromDto(@Nonnull Collection<CategoryDto> dtos) {
    return dtos == null
        ? new HashSet<>()
        : dtos.stream().map(this::fromDto).collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public CategoryDto fromEntity(@Nonnull Category entity) {
    return CategoryDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .link(entity.getLink())
        .build();
  }

  @Nonnull
  @Override
  public Set<CategoryDto> fromEntity(@Nonnull Collection<Category> entities) {
    return entities == null
        ? new HashSet<>()
        : entities.stream().map(this::fromEntity).collect(Collectors.toSet());
  }
}
