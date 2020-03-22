package com.cmsnesia.sdk.client;

import com.cmsnesia.model.EventDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.sdk.client.domain.Page;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import reactor.core.publisher.Mono;

public interface EventService {

  @RequestLine("GET /event/find?page={page}&size={size}")
  @Headers("Content-Type: application/json")
  Mono<Page<EventDto>> find(
      EventDto eventDto, @Param("page") Integer page, @Param("size") Integer size);

  @RequestLine("POST /event/add")
  @Headers("Content-Type: application/json")
  Mono<Result<EventDto>> add(EventDto eventDto);

  @RequestLine("PUT /event/edit")
  @Headers("Content-Type: application/json")
  Mono<Result<EventDto>> edit(EventDto eventDto);

  @RequestLine("PUT /event/delete")
  @Headers("Content-Type: application/json")
  Mono<Result<EventDto>> delete(IdRequest idRequest);
}
