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

  Mono<Page<PostDto>> findDraft(AuthDto authDto, PostDto dto, Pageable pageable);
}
