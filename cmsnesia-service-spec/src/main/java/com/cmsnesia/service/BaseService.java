package com.cmsnesia.service;

import com.cmsnesia.model.AuthDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface BaseService<T> {

    Mono<T> add(AuthDto authDto, T dto);

    Mono<T> edit(AuthDto authDto, T dto);

    Mono<T> delete(AuthDto authDto, T dto);

    Mono<Page<T>> find(AuthDto authDto, T dto, Pageable pageable);

}
