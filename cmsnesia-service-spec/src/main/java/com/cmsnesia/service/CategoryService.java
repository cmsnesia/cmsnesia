package com.cmsnesia.service;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import java.util.Set;
import reactor.core.publisher.Mono;

public interface CategoryService extends BaseService<CategoryDto> {

  Mono<Result<Boolean>> exists(Session session, Set<IdRequest> ids);

  Mono<Result<CategoryDto>> findById(Session session, IdRequest id);

  Mono<Set<CategoryDto>> findByIds(Session session, Set<IdRequest> ids);
}
