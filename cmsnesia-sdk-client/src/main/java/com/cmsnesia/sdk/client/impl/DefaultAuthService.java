package com.cmsnesia.sdk.client.impl;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.request.PageRequest;
import com.cmsnesia.model.response.TokenResponse;
import com.cmsnesia.sdk.client.AuthService;
import com.cmsnesia.sdk.client.AbstractService;
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
