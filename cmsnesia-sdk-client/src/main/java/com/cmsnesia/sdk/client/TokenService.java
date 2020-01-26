package com.cmsnesia.sdk.client;

import com.cmsnesia.model.request.RefreshTokenRequest;
import com.cmsnesia.model.request.TokenRequest;
import com.cmsnesia.model.response.TokenResponse;
import feign.RequestLine;
import reactor.core.publisher.Mono;

public interface TokenService {

    @RequestLine("POST /token/request")
    Mono<TokenResponse> request(TokenRequest request);

    @RequestLine("POST /token/refresh")
    Mono<TokenResponse> refresh(RefreshTokenRequest request);

}
