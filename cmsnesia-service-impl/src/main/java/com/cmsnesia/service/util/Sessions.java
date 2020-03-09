package com.cmsnesia.service.util;

import com.cmsnesia.domain.model.Application;
import com.cmsnesia.model.ApplicationDto;
import com.cmsnesia.model.AuthDto;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class Sessions {

  public static Set<String> applicationIds(AuthDto session) {
    Set<String> appIds =
        session == null || session.getApplications() == null
            ? Collections.emptySet()
            : session.getApplications().stream()
                .map(ApplicationDto::getId)
                .collect(Collectors.toSet());
    return appIds;
  }

  public static Set<Application> applications(AuthDto session) {
    Set<Application> applications =
        session.getApplications() == null
            ? Collections.emptySet()
            : session.getApplications().stream()
                .map(
                    applicationDto ->
                        Application.builder()
                            .id(applicationDto.getId())
                            .name(applicationDto.getName())
                            .build())
                .collect(Collectors.toSet());
    return applications;
  }
}
