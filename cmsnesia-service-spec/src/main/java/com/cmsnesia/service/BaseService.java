package com.cmsnesia.service;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import java.io.Serializable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface BaseService<T extends Serializable> {

  Mono<Result<T>> add(AuthDto session, T dto);

  Mono<Result<T>> edit(AuthDto session, T dto);

  Mono<Result<T>> delete(AuthDto session, T dto);

  Mono<Page<T>> find(AuthDto session, T dto, Pageable pageable);

  Mono<Result<T>> find(AuthDto session, IdRequest idRequest);
}
