package com.cmsnesia.assembler;

import com.cmsnesia.domain.model.Phone;
import com.cmsnesia.model.PhoneDto;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class PhoneAssembler extends Assembler<Phone, PhoneDto> {

  @Nonnull
  @Override
  public Phone fromDto(@Nonnull PhoneDto dto) {
    return Phone.builder()
        .number(dto.getNumber())
        .types(dto.getTypes())
        .status(dto.getStatus())
        .build();
  }

  @Nonnull
  @Override
  public Set<Phone> fromDto(@Nonnull Collection<PhoneDto> list) {
    return list == null
        ? new HashSet<>()
        : list.stream().map(this::fromDto).collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public PhoneDto fromEntity(@Nonnull Phone entity) {
    return PhoneDto.builder()
        .number(entity.getNumber())
        .types(entity.getTypes())
        .status(entity.getStatus())
        .build();
  }

  @Nonnull
  @Override
  public Set<PhoneDto> fromEntity(@Nonnull Collection<Phone> entity) {
    return entity == null
        ? new HashSet<>()
        : entity.stream().map(this::fromEntity).collect(Collectors.toSet());
  }
}
