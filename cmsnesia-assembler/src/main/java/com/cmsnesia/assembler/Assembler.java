package com.cmsnesia.assembler;

import com.cmsnesia.domain.model.Application;
import com.cmsnesia.model.ApplicationDto;
import com.cmsnesia.model.BaseDto;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Assembler<A, B> {

  @Nonnull
  public abstract A fromDto(@Nonnull B dto);

  @Nonnull
  public abstract Collection<A> fromDto(@Nonnull Collection<B> dtos);

  @Nonnull
  public abstract B fromEntity(@Nonnull A entity);

  @Nonnull
  public abstract Collection<B> fromEntity(@Nonnull Collection<A> entities);

  @Nonnull
  protected Set<Application> applicationsFromDto(BaseDto dto) {
    Set<Application> applications =
        dto.getApplications() == null
            ? Collections.emptySet()
            : dto.getApplications().stream()
                .map(
                    applicationDto ->
                        Application.builder()
                            .id(applicationDto.getId())
                            .name(applicationDto.getName())
                            .build())
                .collect(Collectors.toSet());
    return applications;
  }

  @Nonnull
  protected Set<ApplicationDto> applicationsFromModel(BaseDto dto) {
    Set<ApplicationDto> applications =
        dto.getApplications() == null
            ? Collections.emptySet()
            : dto.getApplications().stream()
                .map(
                    model ->
                        ApplicationDto.builder().id(model.getId()).name(model.getName()).build())
                .collect(Collectors.toSet());
    return applications;
  }
}
