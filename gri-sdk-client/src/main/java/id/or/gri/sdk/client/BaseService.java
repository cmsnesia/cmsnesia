package id.or.gri.sdk.client;

import id.or.gri.model.request.PageRequest;
import id.or.gri.model.response.TokenResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BaseService<T> {

    Mono<T> save(TokenResponse tokenResponse, T dto);

    Mono<T> edit(TokenResponse tokenResponse, T dto);

    Mono<T> delete(TokenResponse tokenResponse, T dto);

    Flux<T> find(TokenResponse tokenResponse, T dto, PageRequest pageable);

}
