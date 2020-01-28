package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Auth;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.response.TokenResponse;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthRepoCustom {

  Flux<Auth> find(AuthDto authDto, AuthDto dto, Pageable pageable);

  Mono<Long> countFind(AuthDto authDto, AuthDto dto);

  Mono<Auth> findByAccessTokenAndType(AuthDto authDto, String accessToken, String tokenType);

  Mono<Auth> findByRefreshTokenAndTokenType(AuthDto authDto, String refreshToken, String tokenType);

  Mono<Auth> findByAccessTokenAndRefreshTokenAndTokenType(AuthDto authDto, TokenResponse token);
}
