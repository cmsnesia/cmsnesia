package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Page;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.PageDto;
import com.cmsnesia.model.request.IdRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PageRepoCustom {

  Mono<Page> find(AuthDto session, IdRequest id);

  Flux<Page> find(AuthDto session, PageDto dto, Pageable pageable);

  Mono<Long> countFind(AuthDto session, PageDto dto);

  Mono<Page> findAbout(AuthDto session);
}
