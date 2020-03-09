package com.cmsnesia.assembler;

import com.cmsnesia.domain.CategoryGroup;
import com.cmsnesia.model.CategoryGroupDto;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class CategoryGroupAssembler extends Assembler<CategoryGroup, CategoryGroupDto> {

  @Nonnull
  @Override
  public CategoryGroup fromDto(@Nonnull CategoryGroupDto dto) {
    CategoryGroup category =
        CategoryGroup.builder()
            .id(dto.getId())
            .name(dto.getName())
            .categoryIds(dto.getCategoryIds() == null ? new HashSet<>() : dto.getCategoryIds())
            .build();
    return category;
  }

  @Nonnull
  @Override
  public Set<CategoryGroup> fromDto(@Nonnull Collection<CategoryGroupDto> dtos) {
    return dtos == null
        ? new HashSet<>()
        : dtos.stream().map(this::fromDto).collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public CategoryGroupDto fromEntity(@Nonnull CategoryGroup entity) {
    return CategoryGroupDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .categoryIds(entity.getCategoryIds() == null ? new HashSet<>() : entity.getCategoryIds())
        .build();
  }

  @Nonnull
  @Override
  public Set<CategoryGroupDto> fromEntity(@Nonnull Collection<CategoryGroup> entities) {
    return entities == null
        ? new HashSet<>()
        : entities.stream().map(this::fromEntity).collect(Collectors.toSet());
  }
}
