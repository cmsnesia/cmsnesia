package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Auth;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.response.TokenResponse;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthRepoCustom {

  Mono<Auth> find(AuthDto session, IdRequest id);

  Flux<Auth> find(AuthDto session, AuthDto dto, Pageable pageable);

  Mono<Long> countFind(AuthDto session, AuthDto dto);

  Mono<Auth> findByAccessTokenAndType(AuthDto session, String accessToken, String tokenType);

  Mono<Auth> findByRefreshTokenAndTokenType(AuthDto session, String refreshToken, String tokenType);

  Mono<Auth> findByAccessTokenAndRefreshTokenAndTokenType(AuthDto session, TokenResponse token);

  Mono<Auth> changePassword(AuthDto session, String newPassword);
}
