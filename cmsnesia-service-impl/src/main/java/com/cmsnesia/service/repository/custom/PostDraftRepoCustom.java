package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.PostDraft;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.PostDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostDraftRepoCustom {

  Flux<PostDraft> find(AuthDto authDto, PostDto dto, Pageable pageable);

  Mono<Long> countFind(AuthDto authDto, PostDto dto);
}
