package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Profile;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.request.IdRequest;
import reactor.core.publisher.Mono;

public interface ProfileRepoCustom {

    Mono<Boolean> exists(AuthDto session);

    Mono<Profile> find(AuthDto session, IdRequest idRequest);

}
