package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.model.enums.PostStatus;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.request.IdRequest;
import com.mongodb.client.result.UpdateResult;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepoCustom {

  Mono<Post> find(AuthDto session, IdRequest id);

  Flux<Post> find(AuthDto session, PostDto dto, Pageable pageable);

  Mono<Long> countFind(AuthDto session, PostDto dto);

  Mono<UpdateResult> findAndModifyCategory(AuthDto session, CategoryDto categoryDto);

  Mono<Post> findAndModifyStatus(AuthDto session, IdRequest id, Set<PostStatus> postStatus);

  Mono<Boolean> exists(AuthDto session, String id, String name);
}
