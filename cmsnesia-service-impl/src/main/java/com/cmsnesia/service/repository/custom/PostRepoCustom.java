package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Post;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.request.IdRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepoCustom {

  Mono<Post> find(AuthDto authDto, IdRequest id);

  Flux<Post> find(AuthDto authDto, PostDto dto, Pageable pageable);

  Mono<Long> countFind(AuthDto authDto, PostDto dto);

  Mono<Void> findAndModifyCategory(AuthDto authDto, CategoryDto categoryDto);
}
