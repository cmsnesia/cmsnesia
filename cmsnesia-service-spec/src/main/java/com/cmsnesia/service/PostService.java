package com.cmsnesia.service;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.response.PageResponse;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface PostService extends BaseService<PostDto> {

    Mono<PostDto> publish(AuthDto session, IdRequest id);

    Mono<PageResponse<PostDto>> findDraft(AuthDto authDto, PostDto dto, Pageable pageable);
}
