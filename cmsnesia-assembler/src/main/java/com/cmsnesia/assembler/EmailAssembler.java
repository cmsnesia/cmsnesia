package com.cmsnesia.assembler;

import com.cmsnesia.domain.model.Email;
import com.cmsnesia.model.EmailDto;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class EmailAssembler extends Assembler<Email, EmailDto> {

  @Nonnull
  @Override
  public Email fromDto(@Nonnull EmailDto dto) {
    return Email.builder()
        .address(dto.getAddress())
        .types(dto.getTypes())
        .status(dto.getStatus())
        .build();
  }

  @Nonnull
  @Override
  public Set<Email> fromDto(@Nonnull Collection<EmailDto> list) {
    return list == null
        ? new HashSet<>()
        : list.stream().map(this::fromDto).collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public EmailDto fromEntity(@Nonnull Email entity) {
    return EmailDto.builder()
        .address(entity.getAddress())
        .types(entity.getTypes())
        .status(entity.getStatus())
        .build();
  }

  @Nonnull
  @Override
  public Set<EmailDto> fromEntity(@Nonnull Collection<Email> entity) {
    return entity == null
        ? new HashSet<>()
        : entity.stream().map(this::fromEntity).collect(Collectors.toSet());
  }
}
