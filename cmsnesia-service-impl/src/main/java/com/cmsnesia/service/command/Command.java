package com.cmsnesia.service.command;

import com.cmsnesia.accounts.model.Session;
import lombok.*;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface Command<R, T> {

  Publisher<T> execute(Session session, R request);

  Mono<Boolean
          > validate(Object request);

  @Getter
  @AllArgsConstructor
  class PageableRequest<T> {
    private T data;
    private Pageable pageable;
  }
}
