package com.cmsnesia.service.util;

import com.cmsnesia.model.ApplicationDto;
import com.cmsnesia.model.AuthDto;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class AppsUtil {

  public static Set<String> appIds(AuthDto session) {
    Set<String> appIds =
        session == null || session.getApplications() == null
            ? Collections.emptySet()
            : session.getApplications().stream()
                .map(ApplicationDto::getId)
                .collect(Collectors.toSet());
    return appIds;
  }
}
