package com.cmsnesia.assembler;

import com.cmsnesia.domain.Auth;
import com.cmsnesia.model.AuthDto;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthAssembler implements Assembler<Auth, AuthDto> {

  private final EmailAssembler emailAssembler;

  @Nonnull
  @Override
  public Auth fromDto(@Nonnull AuthDto dto) {
    return Auth.builder()
        .id(dto.getId())
        .username(dto.getUsername())
        .password(dto.getPassword())
        .roles(dto.getRoles())
        .fullName(dto.getFullName())
        .emails(dto.getEmails() == null ? new HashSet<>() : emailAssembler.fromDto(dto.getEmails()))
        .build();
  }

  @Nonnull
  @Override
  public List<Auth> fromDto(@Nonnull Collection<AuthDto> dtos) {
    return dtos.stream().map(this::fromDto).collect(Collectors.toList());
  }

  @Nonnull
  @Override
  public AuthDto fromEntity(@Nonnull Auth entity) {
    return AuthDto.builder()
        .id(entity.getId())
        .username(entity.getUsername())
        .password("******")
        .roles(entity.getRoles())
        .fullName(entity.getFullName())
        .emails(
            entity.getEmails() == null
                ? new HashSet<>()
                : emailAssembler.fromEntity(entity.getEmails()))
        .build();
  }

  @Nonnull
  @Override
  public List<AuthDto> fromEntity(@Nonnull Collection<Auth> entity) {
    return entity.stream().map(this::fromEntity).collect(Collectors.toList());
  }
}
