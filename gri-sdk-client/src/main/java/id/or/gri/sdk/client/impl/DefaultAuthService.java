package id.or.gri.sdk.client.impl;

import id.or.gri.model.AuthDto;
import id.or.gri.model.request.PageRequest;
import id.or.gri.model.response.TokenResponse;
import id.or.gri.sdk.client.AbstractService;
import id.or.gri.sdk.client.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DefaultAuthService extends AbstractService implements AuthService {

    public DefaultAuthService(WebClient webClient) {
        super(webClient);
    }

    @Override
    public Mono<AuthDto> save(TokenResponse tokenResponse, AuthDto dto) {
        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri("/auth/save")
                .body(BodyInserters.fromObject(dto))
                .header(HttpHeaders.AUTHORIZATION, tokenType + tokenResponse.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
        return responseSpec.bodyToMono(AuthDto.class);
    }

    @Override
    public Mono<AuthDto> edit(TokenResponse tokenResponse, AuthDto dto) {
        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri("/auth/edit")
                .body(BodyInserters.fromObject(dto))
                .header(HttpHeaders.AUTHORIZATION, tokenType + tokenResponse.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
        return responseSpec.bodyToMono(AuthDto.class);
    }

    @Override
    public Mono<AuthDto> delete(TokenResponse tokenResponse, AuthDto dto) {
        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri("/auth/delete")
                .body(BodyInserters.fromObject(dto))
                .header(HttpHeaders.AUTHORIZATION, tokenType + tokenResponse.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
        return responseSpec.bodyToMono(AuthDto.class);
    }

    @Override
    public Flux<AuthDto> find(TokenResponse tokenResponse, AuthDto dto, PageRequest pageable) {
        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri("/auth/find?" + pageable.asQueryParam())
                .body(BodyInserters.fromObject(dto))
                .header(HttpHeaders.AUTHORIZATION, tokenType + tokenResponse.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
        return responseSpec.bodyToFlux(AuthDto.class);
    }
}
