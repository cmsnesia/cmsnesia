package com.cmsnesia.service;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.api.Result;
import java.util.Set;
import reactor.core.publisher.Mono;

public interface CategoryService extends BaseService<CategoryDto> {

  Mono<Result<Boolean>> exists(AuthDto session, Set<String> ids);

  Mono<Result<CategoryDto>> findById(AuthDto session, String id);
}
