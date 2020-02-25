package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Menu;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.MenuDto;
import com.cmsnesia.model.request.IdRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MenuRepoCustom {

  Mono<Menu> find(AuthDto authDto, IdRequest id);

  Flux<Menu> find(AuthDto authDto, MenuDto dto, Pageable pageable);

  Mono<Long> countFind(AuthDto authDto, MenuDto dto);

  Mono<Boolean> exists(AuthDto session, String id, String name);
}
