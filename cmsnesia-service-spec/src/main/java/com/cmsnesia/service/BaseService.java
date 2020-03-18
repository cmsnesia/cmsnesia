package com.cmsnesia.service;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import java.io.Serializable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface BaseService<T extends Serializable> {

  Mono<Result<T>> add(Session session, T dto);

  Mono<Result<T>> edit(Session session, T dto);

  Mono<Result<T>> delete(Session session, T dto);

  Mono<Page<T>> find(Session session, T dto, Pageable pageable);

  Mono<Result<T>> find(Session session, IdRequest idRequest);
}
