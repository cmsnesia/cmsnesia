package com.cmsnesia.domain.repository.custom;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.Page;
import com.cmsnesia.model.PageDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface PageRepoCustom {

  Mono<Page> find(Session session, String id);

  Flux<Page> find(Session session, PageDto dto, Pageable pageable);

  Mono<Long> countFind(Session session, PageDto dto);

  Mono<Page> findAbout(Session session);

  Mono<Boolean> exists(Session session, String id, String name);

  Mono<Boolean> exists(Session session, Set<String> ids);
}
