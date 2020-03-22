package com.cmsnesia.assembler;

import com.cmsnesia.domain.Event;
import com.cmsnesia.model.EventDto;
import com.cmsnesia.model.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class EventAssembler extends Assembler<Event, EventDto> {

  private final MediaAssembler mediaAssembler;

  @Nonnull
  @Override
  public Event fromDto(@Nonnull EventDto dto) {
    try {
      return Event.builder()
          .id(dto.getId())
          .name(dto.getName())
          .description(dto.getDescription())
          .medias(mediaAssembler.fromDto(dto.getMedias()))
          .startedAt(DateTimeUtils.toDate(dto.getStartedAt()))
          .endedAt(DateTimeUtils.toDate(dto.getEndedAt()))
          .types(dto.getTypes())
          .build();
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public List<Event> fromDto(@Nonnull Collection<EventDto> dtos) {
    return dtos == null
        ? Collections.emptyList()
        : dtos.stream().map(this::fromDto).collect(Collectors.toList());
  }

  @Nonnull
  @Override
  public EventDto fromEntity(@Nonnull Event entity) {
    return EventDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .description(entity.getDescription())
        .medias(mediaAssembler.fromEntity(entity.getMedias()))
        .startedAt(DateTimeUtils.toString(entity.getStartedAt()))
        .endedAt(DateTimeUtils.toString(entity.getEndedAt()))
        .types(entity.getTypes())
        .build();
  }

  @Nonnull
  @Override
  public List<EventDto> fromEntity(@Nonnull Collection<Event> entities) {
    return entities == null
        ? Collections.emptyList()
        : entities.stream().map(this::fromEntity).collect(Collectors.toList());
  }
}
