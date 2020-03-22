package com.cmsnesia.sdk.client.fallback;

import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.NameRequest;
import com.cmsnesia.sdk.client.CategoryService;
import com.cmsnesia.sdk.client.domain.Page;
import feign.hystrix.FallbackFactory;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;

public class DefaultCategoryServiceFallback implements FallbackFactory<CategoryService> {
  @Override
  public CategoryService create(Throwable throwable) {
    return new CategoryService() {
      @Override
      public Mono<Set<CategoryDto>> findByIds(Set<IdRequest> ids) {
        return Mono.just(Collections.emptySet());
      }

      @Override
      public Mono<Page<CategoryDto>> find(CategoryDto categoryDto, Integer page, Integer size) {
        return Mono.just(new Page<>(Collections.emptyList(), PageRequest.of(page, size), 0L));
      }

      @Override
      public Mono<Result<CategoryDto>> add(NameRequest nameRequest) {
        return Mono.just(Result.build(StatusCode.SAVE_FAILED));
      }

      @Override
      public Mono<Result<CategoryDto>> edit(CategoryDto categoryDto) {
        return Mono.just(Result.build(StatusCode.SAVE_FAILED));
      }

      @Override
      public Mono<Result<CategoryDto>> delete(IdRequest idRequest) {
        return Mono.just(Result.build(StatusCode.DELETE_FAILED));
      }
    };
  }
}
