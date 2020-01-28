package com.cmsnesia.service;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import java.util.Set;
import reactor.core.publisher.Mono;

public interface CategoryService extends BaseService<CategoryDto> {

  Mono<Boolean> exists(AuthDto session, Set<String> ids);

  Mono<CategoryDto> findById(AuthDto session, String id);
}
