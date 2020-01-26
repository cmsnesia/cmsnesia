package com.cmsnesia.sdk.client;

import com.cmsnesia.model.request.RefreshTokenRequest;
import com.cmsnesia.model.request.TokenRequest;
import com.cmsnesia.model.response.TokenResponse;
import reactor.core.publisher.Mono;

public interface TokenService {

    Mono<TokenResponse> request(TokenRequest tokenRequest);

    Mono<TokenResponse> refresh(RefreshTokenRequest tokenResponse);

}
