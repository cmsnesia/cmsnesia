package com.cmsnesia.sdk.client;

import com.cmsnesia.model.MenuDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.sdk.client.domain.Page;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import reactor.core.publisher.Mono;

public interface MenuService {

  @RequestLine("GET /menu/find?page={page}&size={size}")
  @Headers("Content-Type: application/json")
  Mono<Page<MenuDto>> find(
      MenuDto menuDto, @Param("page") Integer page, @Param("size") Integer size);

  @RequestLine("POST /menu/add")
  @Headers("Content-Type: application/json")
  Mono<Result<MenuDto>> add(MenuDto menuDto);

  @RequestLine("PUT /menu/edit")
  @Headers("Content-Type: application/json")
  Mono<Result<MenuDto>> edit(MenuDto menuDto);

  @RequestLine("PUT /menu/delete")
  @Headers("Content-Type: application/json")
  Mono<Result<MenuDto>> delete(IdRequest idRequest);
}
