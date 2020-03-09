package com.cmsnesia.assembler;

import com.cmsnesia.domain.model.Author;
import com.cmsnesia.model.AuthorDto;
import com.cmsnesia.model.util.DateTimeUtils;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class AuthorAssembler extends Assembler<Author, AuthorDto> {

  @Nonnull
  @Override
  public Author fromDto(@Nonnull AuthorDto dto) {
    try {
      return Author.builder()
          .name(dto.getName())
          .modifiedAt(DateTimeUtils.toDate(dto.getModifiedAt()))
          .build();
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public Set<Author> fromDto(@Nonnull Collection<AuthorDto> dtos) {
    return dtos == null
        ? new HashSet<>()
        : dtos.stream().map(this::fromDto).collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public AuthorDto fromEntity(@Nonnull Author entity) {
    return AuthorDto.builder()
        .name(entity.getName())
        .modifiedAt(DateTimeUtils.toString(entity.getModifiedAt()))
        .build();
  }

  @Nonnull
  @Override
  public Set<AuthorDto> fromEntity(@Nonnull Collection<Author> entities) {
    return entities == null
        ? new HashSet<>()
        : entities.stream().map(this::fromEntity).collect(Collectors.toSet());
  }
}
