package com.cmsnesia.assembler;

import com.cmsnesia.domain.Menu;
import com.cmsnesia.domain.model.Category;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.MenuDto;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class MenuAssembler extends Assembler<Menu, MenuDto> {

  @Nonnull
  @Override
  public Menu fromDto(@Nonnull MenuDto dto) {
    Menu menu =
        Menu.builder()
            .id(dto.getId())
            .name(dto.getName())
            .categories(fromCategoryDto(dto.getCategories()))
            .build();
    return menu;
  }

  @Nonnull
  @Override
  public Set<Menu> fromDto(@Nonnull Collection<MenuDto> dtos) {
    return dtos == null
        ? new HashSet<>()
        : dtos.stream().map(this::fromDto).collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public MenuDto fromEntity(@Nonnull Menu entity) {
    return MenuDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .categories(fromCategoryModel(entity.getCategories()))
        .build();
  }

  @Nonnull
  @Override
  public Set<MenuDto> fromEntity(@Nonnull Collection<Menu> entities) {
    return entities == null
        ? new HashSet<>()
        : entities.stream().map(this::fromEntity).collect(Collectors.toSet());
  }

  private Set<CategoryDto> fromCategoryModel(Set<Category> categories) {
    if (categories == null) {
      return Collections.emptySet();
    }
    return categories.stream()
        .map(
            category -> {
              return CategoryDto.builder().id(category.getId()).name(category.getName()).build();
            })
        .collect(Collectors.toSet());
  }

  private Set<Category> fromCategoryDto(Set<CategoryDto> categories) {
    if (categories == null) {
      return Collections.emptySet();
    }
    return categories.stream()
        .map(
            categoryDto ->
                Category.builder().id(categoryDto.getId()).name(categoryDto.getName()).build())
        .collect(Collectors.toSet());
  }
}
