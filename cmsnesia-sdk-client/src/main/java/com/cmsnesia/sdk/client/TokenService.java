package com.cmsnesia.sdk.client;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.request.RefreshTokenRequest;
import com.cmsnesia.model.request.TokenRequest;
import com.cmsnesia.model.response.TokenResponse;
import feign.Headers;
import feign.RequestLine;
import reactor.core.publisher.Mono;

public interface TokenService {

  @RequestLine("POST /token/request")
  @Headers("Content-Type: application/json")
  Mono<TokenResponse> request(TokenRequest request);

  @RequestLine("PUT /token/refresh")
  @Headers("Content-Type: application/json")
  Mono<TokenResponse> refresh(RefreshTokenRequest request);

  @RequestLine("DELETE /token/destroy")
  @Headers("Content-Type: application/json")
  Mono<TokenResponse> destroy(TokenResponse request);

  @RequestLine("POST /token/validate")
  @Headers("Content-Type: application/json")
  Mono<AuthDto> validate(TokenResponse request);
}
