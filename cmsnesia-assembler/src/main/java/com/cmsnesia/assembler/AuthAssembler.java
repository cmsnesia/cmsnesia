package com.cmsnesia.assembler;

import com.cmsnesia.domain.Auth;
import com.cmsnesia.domain.model.Application;
import com.cmsnesia.model.ApplicationDto;
import com.cmsnesia.model.AuthDto;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
public class AuthAssembler extends Assembler<Auth, AuthDto> {

  private final EmailAssembler emailAssembler;

  @Nonnull
  @Override
  public Auth fromDto(@Nonnull AuthDto dto) {
    Auth auth =
        Auth.builder()
            .id(dto.getId())
            .username(dto.getUsername())
            .password(dto.getPassword())
            .roles(dto.getRoles())
            .fullName(dto.getFullName())
            .emails(
                dto.getEmails() == null ? new HashSet<>() : emailAssembler.fromDto(dto.getEmails()))
            .build();
    auth.setApplications(
        dto.getApplications() == null
            ? Collections.emptySet()
            : dto.getApplications().stream()
                .filter(application -> StringUtils.hasText(application.getId()))
                .map(
                    application ->
                        Application.builder()
                            .id(application.getId())
                            .name(application.getName())
                            .build())
                .collect(Collectors.toSet()));
    return auth;
  }

  @Nonnull
  @Override
  public Set<Auth> fromDto(@Nonnull Collection<AuthDto> dtos) {
    return dtos == null
        ? new HashSet<>()
        : dtos.stream().map(this::fromDto).collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public AuthDto fromEntity(@Nonnull Auth entity) {
    AuthDto dto =
        AuthDto.builder()
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
    dto.setApplications(
        entity.getApplications() == null
            ? Collections.emptySet()
            : entity.getApplications().stream()
                .filter(application -> StringUtils.hasText(application.getId()))
                .map(
                    application ->
                        ApplicationDto.builder()
                            .id(application.getId())
                            .name(application.getName())
                            .build())
                .collect(Collectors.toSet()));
    return dto;
  }

  @Nonnull
  @Override
  public Set<AuthDto> fromEntity(@Nonnull Collection<Auth> entity) {
    return entity == null
        ? new HashSet<>()
        : entity.stream().map(this::fromEntity).collect(Collectors.toSet());
  }
}
