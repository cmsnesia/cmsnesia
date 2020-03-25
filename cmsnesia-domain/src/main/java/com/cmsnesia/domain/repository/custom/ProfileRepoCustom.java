package com.cmsnesia.domain.repository.custom;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.Profile;
import com.cmsnesia.model.request.IdRequest;
import reactor.core.publisher.Mono;

public interface ProfileRepoCustom {

  Mono<Boolean> exists(Session session);

  Mono<Profile> find(Session session, IdRequest idRequest);
}
