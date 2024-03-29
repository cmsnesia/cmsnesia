package com.cmsnesia.domain.repository.custom;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.CategoryGroup;
import com.cmsnesia.model.CategoryGroupDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface CategoryGroupRepoCustom {

  Mono<CategoryGroup> find(Session session, String id);

  Flux<CategoryGroup> find(Session session, CategoryGroupDto dto, Pageable pageable);

  Mono<Long> countFind(Session session, CategoryGroupDto dto);

  Mono<Boolean> exists(Session session, String id, String name);

  Mono<Boolean> exists(Session session, Set<String> ids);
}
