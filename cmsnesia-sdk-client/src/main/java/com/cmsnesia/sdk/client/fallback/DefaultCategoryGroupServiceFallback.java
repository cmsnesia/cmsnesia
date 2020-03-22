package com.cmsnesia.sdk.client.fallback;

import com.cmsnesia.model.CategoryGroupDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.sdk.client.CategoryGroupService;
import com.cmsnesia.sdk.client.domain.Page;
import feign.hystrix.FallbackFactory;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class DefaultCategoryGroupServiceFallback implements FallbackFactory<CategoryGroupService> {
  @Override
  public CategoryGroupService create(Throwable throwable) {
    return new CategoryGroupService() {
      @Override
      public Mono<Page<CategoryGroupDto>> find(
          CategoryGroupDto categoryGroupDto, Integer page, Integer size) {
        return Mono.just(new Page<>(Collections.emptyList(), PageRequest.of(page, size), 0L));
      }

      @Override
      public Mono<Result<CategoryGroupDto>> add(CategoryGroupDto categoryGroupDto) {
        return Mono.just(Result.build(StatusCode.SAVE_FAILED));
      }

      @Override
      public Mono<Result<CategoryGroupDto>> edit(CategoryGroupDto categoryGroupDto) {
        return Mono.just(Result.build(StatusCode.SAVE_FAILED));
      }

      @Override
      public Mono<Result<CategoryGroupDto>> delete(IdRequest idRequest) {
        return Mono.just(Result.build(StatusCode.DELETE_FAILED));
      }
    };
  }
}
