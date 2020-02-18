package com.cmsnesia.assembler;

import com.cmsnesia.domain.Category;
import com.cmsnesia.model.CategoryDto;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class CategoryAssembler extends Assembler<Category, CategoryDto> {

  @Nonnull
  @Override
  public Category fromDto(@Nonnull CategoryDto dto) {
    Category category = Category.builder().id(dto.getId()).name(dto.getName()).build();
    return category;
  }

  @Nonnull
  @Override
  public Set<Category> fromDto(@Nonnull Collection<CategoryDto> dtos) {
    return dtos.stream().map(this::fromDto).collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public CategoryDto fromEntity(@Nonnull Category entity) {
    return CategoryDto.builder().id(entity.getId()).name(entity.getName()).build();
  }

  @Nonnull
  @Override
  public Set<CategoryDto> fromEntity(@Nonnull Collection<Category> entities) {
    return entities.stream().map(this::fromEntity).collect(Collectors.toSet());
  }
}
