package com.cmsnesia.sdk.client.fallback;

import com.cmsnesia.model.*;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.sdk.client.PublicService;
import com.cmsnesia.sdk.client.domain.Page;
import feign.hystrix.FallbackFactory;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class DefaultPublicServiceFallback implements FallbackFactory<PublicService> {

  @Override
  public PublicService create(Throwable throwable) {
    return new PublicService() {
      @Override
      public Mono<Page<PostDto>> findPosts(PostDto postDto, Integer page, Integer size) {
        return Mono.just(new Page<>(Collections.emptyList(), PageRequest.of(page, size), 0L));
      }

      @Override
      public Mono<Page<PostDto>> findPopularPosts(PostDto postDto, Integer page, Integer size) {
        return Mono.just(new Page<>(Collections.emptyList(), PageRequest.of(page, size), 0L));
      }

      @Override
      public Mono<Result<PostDto>> findPostById(IdRequest id) {
        return Mono.just(Result.build(StatusCode.DATA_FOUND));
      }

      @Override
      public Mono<Page<CategoryDto>> findCategories(
          CategoryDto categoryDto, Integer page, Integer size) {
        return Mono.just(new Page<>(Collections.emptyList(), PageRequest.of(page, size), 0L));
      }

      @Override
      public Mono<Page<CategoryGroupDto>> findCategoryGroups(
          CategoryGroupDto categoryGroupDto, Integer page, Integer size) {
        return Mono.just(new Page<>(Collections.emptyList(), PageRequest.of(page, size), 0L));
      }

      @Override
      public Mono<Page<MenuDto>> findMenus(MenuDto menuDto, Integer page, Integer size) {
        return Mono.just(new Page<>(Collections.emptyList(), PageRequest.of(page, size), 0L));
      }

      @Override
      public Mono<Page<PageDto>> findPages(PageDto pageDto, Integer page, Integer size) {
        return Mono.just(new Page<>(Collections.emptyList(), PageRequest.of(page, size), 0L));
      }

      @Override
      public Mono<Result<ProfileDto>> findProfile() {
        return Mono.just(Result.build(StatusCode.DATA_FOUND));
      }

      @Override
      public Mono<Result<PageDto>> findPageById(IdRequest id) {
        return Mono.just(Result.build(StatusCode.DATA_FOUND));
      }
    };
  }
}
