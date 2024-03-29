package com.cmsnesia.assembler;

import com.cmsnesia.domain.model.Media;
import com.cmsnesia.domain.model.enums.MediaType;
import com.cmsnesia.model.MediaDto;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class MediaAssembler extends Assembler<Media, MediaDto> {

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
    return dtos == null
        ? new HashSet<>()
        : dtos.stream().map(this::fromDto).collect(Collectors.toSet());
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
    return entities == null
        ? new HashSet<>()
        : entities.stream().map(this::fromEntity).collect(Collectors.toSet());
  }
}
