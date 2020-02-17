package com.cmsnesia.sdk.client;

import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.NameRequest;
import com.cmsnesia.sdk.client.domain.Page;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import reactor.core.publisher.Mono;

public interface MenuService {

  @RequestLine("GET /menu/find?page={page}&size={size}")
  @Headers("Content-Type: application/json")
  Mono<Page<CategoryDto>> find(
      CategoryDto categoryDto, @Param("page") Integer page, @Param("size") Integer size);

  @RequestLine("POST /menu/add")
  @Headers("Content-Type: application/json")
  Mono<Result<CategoryDto>> add(NameRequest nameRequest);

  @RequestLine("PUT /menu/edit")
  @Headers("Content-Type: application/json")
  Mono<Result<CategoryDto>> edit(CategoryDto categoryDto);

  @RequestLine("PUT /menu/delete")
  @Headers("Content-Type: application/json")
  Mono<Result<CategoryDto>> delete(IdRequest idRequest);
}
