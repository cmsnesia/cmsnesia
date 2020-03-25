package com.cmsnesia.domain.repository.custom;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.Event;
import com.cmsnesia.model.EventDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface EventRepoCustom {

  Mono<Event> find(Session session, String id);

  Flux<Event> find(Session session, Set<String> ids);

  Flux<Event> find(Session session, EventDto dto, Pageable pageable);

  Mono<Long> countFind(Session session, EventDto dto);

  Mono<Boolean> exists(Session session, Set<String> ids);

  Mono<Boolean> exists(Session session, String id, String name);
}
