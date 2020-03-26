package com.cmsnesia.service.command;

import com.cmsnesia.accounts.model.Session;
import lombok.*;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Pageable;

public interface Command<R, T> {

  Publisher<T> execute(Session session, R request);

  @Getter
  @AllArgsConstructor
  class PageableRequest<T> {
    private T data;
    private Pageable pageable;
  }
}
