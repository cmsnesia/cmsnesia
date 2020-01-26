package com.cmsnesia.assembler;

import cmsnesia.domain.Tag;
import com.cmsnesia.model.TagDto;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Deprecated
public class TagAssembler implements Assembler<Tag, TagDto> {

    @Nonnull
    @Override
    public Tag fromDto(@Nonnull TagDto dto) {
        return Tag.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    @Nonnull
    @Override
    public Set<Tag> fromDto(@Nonnull Collection<TagDto> dtos) {
        return dtos.stream().map(this::fromDto).collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public TagDto fromEntity(@Nonnull Tag entity) {
        return TagDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    @Nonnull
    @Override
    public Set<TagDto> fromEntity(@Nonnull Collection<Tag> entities) {
        return entities.stream().map(this::fromEntity).collect(Collectors.toSet());
    }
}
