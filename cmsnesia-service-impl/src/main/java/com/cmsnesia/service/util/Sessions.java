package com.cmsnesia.service.util;

import com.cmsnesia.accounts.model.ApplicationDto;
import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.model.Application;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class Sessions {

  public static Set<String> applicationIds(Session session) {
    Set<String> appIds =
        session == null || session.getApplications() == null
            ? Collections.emptySet()
            : session.getApplications().stream()
                .map(ApplicationDto::getId)
                .collect(Collectors.toSet());
    return appIds;
  }

  public static Set<Application> applications(Session session) {
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
