package com.cmsnesia.sdk.client.fallback;

import com.cmsnesia.model.EventDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.sdk.client.EventService;
import com.cmsnesia.sdk.client.domain.Page;
import feign.hystrix.FallbackFactory;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class DefaultEventServiceFallback implements FallbackFactory<EventService> {

  @Override
  public EventService create(Throwable throwable) {
    return new EventService() {
      @Override
      public Mono<Page<EventDto>> find(EventDto eventDto, Integer page, Integer size) {
        return Mono.just(new Page<>(Collections.emptyList(), PageRequest.of(page, size), 0L));
      }

      @Override
      public Mono<Result<EventDto>> add(EventDto eventDto) {
        return Mono.just(Result.build(StatusCode.SAVE_FAILED));
      }

      @Override
      public Mono<Result<EventDto>> edit(EventDto eventDto) {
        return Mono.just(Result.build(StatusCode.SAVE_FAILED));
      }

      @Override
      public Mono<Result<EventDto>> delete(IdRequest idRequest) {
        return Mono.just(Result.build(StatusCode.DELETE_FAILED));
      }
    };
  }
}
