package com.cmsnesia.service;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.api.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.io.Serializable;

public interface BaseService<T extends Serializable> {

  Mono<Result<T>> add(AuthDto authDto, T dto);

  Mono<Result<T>> edit(AuthDto authDto, T dto);

  Mono<Result<T>> delete(AuthDto authDto, T dto);

  Mono<Page<T>> find(AuthDto authDto, T dto, Pageable pageable);
}
