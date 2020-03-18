package com.cmsnesia.service;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.model.MenuDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import reactor.core.publisher.Mono;

public interface MenuService extends BaseService<MenuDto> {

  Mono<Result<MenuDto>> findById(Session session, IdRequest id);
}
