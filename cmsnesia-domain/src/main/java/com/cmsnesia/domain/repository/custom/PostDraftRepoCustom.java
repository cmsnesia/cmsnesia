package com.cmsnesia.domain.repository.custom;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.PostDraft;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.request.IdRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface PostDraftRepoCustom {

  Mono<PostDraft> find(Session session, IdRequest id);

  Flux<PostDraft> find(Session session, PostDto dto, Pageable pageable);

  Mono<Long> countFind(Session session, PostDto dto);

  Mono<PostDraft> deleteById(Session session, IdRequest idRequest);

  Mono<Boolean> exists(Session session, String id, String name);

  Mono<Boolean> exists(Session session, Set<String> ids);
}
