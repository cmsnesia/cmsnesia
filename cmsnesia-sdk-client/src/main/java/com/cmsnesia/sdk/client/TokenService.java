package com.cmsnesia.sdk.client;

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

    @RequestLine("POST /token/refresh")
    @Headers("Content-Type: application/json")
    Mono<TokenResponse> refresh(RefreshTokenRequest request);

    @RequestLine("POST /token/destroy")
    @Headers("Content-Type: application/json")
    Mono<TokenResponse> destroy(TokenResponse request);

}
