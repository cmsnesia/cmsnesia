package com.cmsnesia.sdk.client.fallback;

import com.cmsnesia.model.MenuDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.sdk.client.MenuService;
import com.cmsnesia.sdk.client.domain.Page;
import feign.hystrix.FallbackFactory;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class DefaultMenuServiceFallback implements FallbackFactory<MenuService> {

  @Override
  public MenuService create(Throwable throwable) {
    return new MenuService() {
      @Override
      public Mono<Page<MenuDto>> find(MenuDto menuDto, Integer page, Integer size) {
        return Mono.just(new Page<>(Collections.emptyList(), PageRequest.of(page, size), 0L));
      }

      @Override
      public Mono<Result<MenuDto>> add(MenuDto menuDto) {
        return Mono.just(Result.build(StatusCode.SAVE_FAILED));
      }

      @Override
      public Mono<Result<MenuDto>> edit(MenuDto menuDto) {
        return Mono.just(Result.build(StatusCode.SAVE_FAILED));
      }

      @Override
      public Mono<Result<MenuDto>> delete(IdRequest idRequest) {
        return Mono.just(Result.build(StatusCode.DELETE_FAILED));
      }
    };
  }
}
