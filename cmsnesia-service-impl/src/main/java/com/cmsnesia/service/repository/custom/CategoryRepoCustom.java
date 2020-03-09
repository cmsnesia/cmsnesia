package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Category;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.request.IdRequest;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryRepoCustom {

  Mono<Category> find(AuthDto session, IdRequest id);

  Flux<Category> find(AuthDto session, CategoryDto dto, Pageable pageable);

  Mono<Long> countFind(AuthDto session, CategoryDto dto);

  Mono<Boolean> exists(AuthDto session, Set<String> ids);

  Mono<Boolean> exists(AuthDto session, String id, String name);
}
