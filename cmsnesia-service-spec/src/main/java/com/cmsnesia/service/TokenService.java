package com.cmsnesia.service;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.request.RefreshTokenRequest;
import com.cmsnesia.model.request.TokenRequest;
import com.cmsnesia.model.response.TokenResponse;
import reactor.core.publisher.Mono;

public interface TokenService {

  Mono<AuthDto> validate(TokenResponse tokenResponse);

  Mono<TokenResponse> request(TokenRequest tokenRequest);

  Mono<TokenResponse> refresh(RefreshTokenRequest tokenResponse);

  Mono<String> destroy(TokenResponse tokenResponse);
}
