package id.or.gri.service;

import id.or.gri.model.AuthDto;
import id.or.gri.model.request.RefreshTokenRequest;
import id.or.gri.model.request.TokenRequest;
import id.or.gri.model.response.TokenResponse;
import reactor.core.publisher.Mono;

public interface TokenService {

    Mono<AuthDto> decode(TokenResponse tokenResponse);

    Mono<TokenResponse> encode(TokenRequest tokenRequest);

    Mono<TokenResponse> refresh(RefreshTokenRequest tokenResponse);

    Mono<String> destroy(TokenResponse tokenResponse);

}
