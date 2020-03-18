package com.cmsnesia.service.repository.custom;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.Menu;
import com.cmsnesia.model.MenuDto;
import com.cmsnesia.model.request.IdRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MenuRepoCustom {

  Mono<Menu> find(Session session, IdRequest id);

  Flux<Menu> find(Session session, MenuDto dto, Pageable pageable);

  Mono<Long> countFind(Session session, MenuDto dto);

  Mono<Boolean> exists(Session session, String id, String name);
}
