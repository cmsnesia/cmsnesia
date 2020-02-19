package com.cmsnesia.service;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.PageDto;
import com.cmsnesia.model.api.Result;
import reactor.core.publisher.Mono;

public interface PageService extends BaseService<PageDto> {

  Mono<Result<PageDto>> findAbout(AuthDto session);
}
