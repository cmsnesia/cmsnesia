package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.CategoryGroup;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryGroupDto;
import com.cmsnesia.model.request.IdRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryGroupRepoCustom {

  Mono<CategoryGroup> find(AuthDto session, IdRequest id);

  Flux<CategoryGroup> find(AuthDto session, CategoryGroupDto dto, Pageable pageable);

  Mono<Long> countFind(AuthDto session, CategoryGroupDto dto);

  Mono<Boolean> exists(AuthDto session, String id, String name);
}
