package com.cmsnesia.assembler;

import cmsnesia.domain.Category;
import com.cmsnesia.model.CategoryDto;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CategoryAssembler implements Assembler<Category, CategoryDto> {

    @Nonnull
    @Override
    public Category fromDto(@Nonnull CategoryDto dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    @Nonnull
    @Override
    public Set<Category> fromDto(@Nonnull Collection<CategoryDto> dtos) {
        return dtos.stream().map(this::fromDto).collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public CategoryDto fromEntity(@Nonnull Category entity) {
        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    @Nonnull
    @Override
    public Set<CategoryDto> fromEntity(@Nonnull Collection<Category> entities) {
        return entities.stream().map(this::fromEntity).collect(Collectors.toSet());
    }

}
