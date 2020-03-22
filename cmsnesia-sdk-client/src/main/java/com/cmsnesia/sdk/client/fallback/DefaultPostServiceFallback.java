package com.cmsnesia.sdk.client.fallback;

import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.PostRequest;
import com.cmsnesia.sdk.client.PostService;
import com.cmsnesia.sdk.client.domain.Page;
import feign.hystrix.FallbackFactory;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class DefaultPostServiceFallback implements FallbackFactory<PostService> {

  @Override
  public PostService create(Throwable throwable) {
    return new PostService() {
      @Override
      public Mono<Page<PostDto>> find(PostDto postDto, Integer page, Integer size) {
        return Mono.just(new Page<>(Collections.emptyList(), PageRequest.of(page, size), 0L));
      }

      @Override
      public Mono<Page<PostDto>> findDraft(PostDto postDto, Integer page, Integer size) {
        return Mono.just(new Page<>(Collections.emptyList(), PageRequest.of(page, size), 0L));
      }

      @Override
      public Mono<Result<PostDto>> add(PostRequest postRequest) {
        return Mono.just(Result.build(StatusCode.SAVE_FAILED));
      }

      @Override
      public Mono<Result<PostDto>> edit(PostRequest postRequest) {
        return Mono.just(Result.build(StatusCode.SAVE_FAILED));
      }

      @Override
      public Mono<Result<PostDto>> editDraft(PostRequest postRequest) {
        return Mono.just(Result.build(StatusCode.SAVE_FAILED));
      }

      @Override
      public Mono<Result<PostDto>> delete(IdRequest idRequest) {
        return Mono.just(Result.build(StatusCode.DELETE_FAILED));
      }

      @Override
      public Mono<Result<PostDto>> publish(IdRequest idRequest) {
        return Mono.just(Result.build(StatusCode.SAVE_FAILED));
      }
    };
  }
}
