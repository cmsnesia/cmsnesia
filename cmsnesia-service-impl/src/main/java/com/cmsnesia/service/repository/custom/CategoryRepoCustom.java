package com.cmsnesia.service.repository.custom;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.Category;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.request.IdRequest;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryRepoCustom {

  Mono<Category> find(Session session, IdRequest id);

  Flux<Category> find(Session session, Set<IdRequest> ids);

  Flux<Category> find(Session session, CategoryDto dto, Pageable pageable);

  Mono<Long> countFind(Session session, CategoryDto dto);

  Mono<Boolean> exists(Session session, Set<String> ids);

  Mono<Boolean> exists(Session session, String id, String name);
}
