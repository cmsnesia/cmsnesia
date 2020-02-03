package com.cmsnesia.service;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface PostService extends BaseService<PostDto> {

  Mono<Result<PostDto>> publish(AuthDto session, IdRequest id);

  Mono<Result<PostDto>> editDraft(AuthDto session, PostDto dto);

  Mono<Page<PostDto>> findDraft(AuthDto session, PostDto dto, Pageable pageable);

  Mono<Result<PostDto>> findDraft(AuthDto session, IdRequest idRequest);
}
