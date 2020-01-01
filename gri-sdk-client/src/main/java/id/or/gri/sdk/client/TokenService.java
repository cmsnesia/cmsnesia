package id.or.gri.sdk.client;

import id.or.gri.model.AuthDto;
import id.or.gri.model.request.RefreshTokenRequest;
import id.or.gri.model.request.TokenRequest;
import id.or.gri.model.response.TokenResponse;
import reactor.core.publisher.Mono;

public interface TokenService {

    Mono<TokenResponse> request(TokenRequest tokenRequest);

    Mono<TokenResponse> refresh(RefreshTokenRequest tokenResponse);

}
