package id.or.gri.sdk.client.impl;

import id.or.gri.model.request.RefreshTokenRequest;
import id.or.gri.model.request.TokenRequest;
import id.or.gri.model.response.TokenResponse;
import id.or.gri.sdk.client.AbstractService;
import id.or.gri.sdk.client.TokenService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DefaultTokenService extends AbstractService implements TokenService {

    public DefaultTokenService(WebClient webClient) {
        super(webClient);
    }

    @Override
    public Mono<TokenResponse> request(TokenRequest tokenRequest) {
        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri("/token/request")
                .body(BodyInserters.fromObject(tokenRequest))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
        return responseSpec.bodyToMono(TokenResponse.class);
    }

    @Override
    public Mono<TokenResponse> refresh(RefreshTokenRequest tokenResponse) {
        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri("/token/refresh")
                .body(BodyInserters.fromObject(tokenResponse))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
        return responseSpec.bodyToMono(TokenResponse.class);
    }
}