package com.cmsnesia.assembler;

import com.cmsnesia.domain.Page;
import com.cmsnesia.domain.model.Tag;
import com.cmsnesia.model.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PageAssembler extends Assembler<Page, PageDto> {

  private final AuthorAssembler authorAssembler;
  private final MediaAssembler mediaAssembler;

  @Nonnull
  @Override
  public Page fromDto(@Nonnull PageDto dto) {
    Set<Tag> tags = new HashSet<>();
    Page post =
        Page.builder()
            .id(dto.getId())
            .name(dto.getName())
            .content(dto.getContent())
            .authors(
                dto.getAuthors() == null
                    ? new HashSet<>()
                    : authorAssembler.fromDto(dto.getAuthors()))
            .medias(
                dto.getMedias() == null ? new HashSet<>() : mediaAssembler.fromDto(dto.getMedias()))
            .build();
    return post;
  }

  @Nonnull
  @Override
  public Set<Page> fromDto(@Nonnull Collection<PageDto> dtos) {
    return dtos.stream().map(this::fromDto).collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public PageDto fromEntity(@Nonnull Page entity) {
    return PageDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .content(entity.getContent())
        .authors(
            entity.getAuthors() == null
                ? new HashSet<>()
                : authorAssembler.fromEntity(entity.getAuthors()))
        .medias(
            entity.getMedias() == null
                ? new HashSet<>()
                : mediaAssembler.fromEntity(entity.getMedias()))
        .build();
  }

  @Nonnull
  @Override
  public Set<PageDto> fromEntity(@Nonnull Collection<Page> entities) {
    return entities.stream().map(this::fromEntity).collect(Collectors.toSet());
  }
}
