package com.cmsnesia.domain.repository.custom;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.Menu;
import com.cmsnesia.model.MenuDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface MenuRepoCustom {

  Mono<Menu> find(Session session, String id);

  Flux<Menu> find(Session session, MenuDto dto, Pageable pageable);

  Mono<Long> countFind(Session session, MenuDto dto);

  Mono<Boolean> exists(Session session, String id, String name);

  Mono<Boolean> exists(Session session, Set<String> ids);
}
