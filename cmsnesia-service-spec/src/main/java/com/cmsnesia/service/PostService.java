package com.cmsnesia.service;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface PostService extends BaseService<PostDto> {

  Mono<Result<PostDto>> publish(Session session, IdRequest id);

  Mono<Result<PostDto>> editDraft(Session session, PostDto dto);

  Mono<Page<PostDto>> findDraft(Session session, PostDto dto, Pageable pageable);

  Mono<Result<PostDto>> findDraft(Session session, IdRequest idRequest);

  Mono<Result<PostDto>> deleteDraft(Session session, PostDto dto);

  Mono<Result<PostDto>> findByIdOrLink(Session session, PostDto dto);
}
