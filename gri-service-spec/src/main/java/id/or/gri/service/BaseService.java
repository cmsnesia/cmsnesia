package id.or.gri.service;

import id.or.gri.model.AuthDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BaseService<T> {

    Mono<T> add(AuthDto authDto, T dto);

    Mono<T> edit(AuthDto authDto, T dto);

    Mono<T> delete(AuthDto authDto, T dto);

    Flux<T> find(AuthDto authDto, T dto, Pageable pageable);

}
