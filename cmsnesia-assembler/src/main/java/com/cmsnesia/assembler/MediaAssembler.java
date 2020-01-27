package com.cmsnesia.assembler;

import com.cmsnesia.domain.model.Media;
import com.cmsnesia.domain.model.enums.MediaType;
import com.cmsnesia.model.MediaDto;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MediaAssembler implements Assembler<Media, MediaDto> {

    @Nonnull
    @Override
    public Media fromDto(@Nonnull MediaDto dto) {
        return Media.builder()
                .name(dto.getName())
                .url(dto.getUrl())
                .type(MediaType.valueOf(dto.getType()))
                .build();
    }

    @Nonnull
    @Override
    public Set<Media> fromDto(@Nonnull Collection<MediaDto> dtos) {
        return dtos.stream().map(this::fromDto).collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public MediaDto fromEntity(@Nonnull Media entity) {
        return MediaDto.builder()
                .name(entity.getName())
                .url(entity.getUrl())
                .type(entity.getType().name())
                .build();
    }

    @Nonnull
    @Override
    public Set<MediaDto> fromEntity(@Nonnull Collection<Media> entities) {
        return entities.stream().map(this::fromEntity).collect(Collectors.toSet());
    }
}
