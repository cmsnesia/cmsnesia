package com.cmsnesia.domain.repository.custom;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.model.enums.PostStatus;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.PostDto;
import com.mongodb.client.result.UpdateResult;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepoCustom {

  Mono<Post> find(Session session, String id, String link);

  Flux<Post> find(Session session, PostDto dto, Pageable pageable);

  Mono<Long> countFind(Session session, PostDto dto);

  Mono<UpdateResult> findAndModifyCategory(Session session, CategoryDto categoryDto);

  Mono<Post> findAndModifyStatus(Session session, String id, Set<PostStatus> postStatus);

  Mono<Boolean> exists(Session session, String id, String name, String link);

  Mono<Boolean> exists(Session session, Set<String> ids);
}
