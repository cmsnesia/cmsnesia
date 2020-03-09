package com.cmsnesia.sdk.client;

import com.cmsnesia.model.CategoryGroupDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.sdk.client.domain.Page;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import reactor.core.publisher.Mono;

public interface CategoryGroupService {

  @RequestLine("GET /category/group/find?page={page}&size={size}")
  @Headers("Content-Type: application/json")
  Mono<Page<CategoryGroupDto>> find(
          CategoryGroupDto categoryGroupDto, @Param("page") Integer page, @Param("size") Integer size);

  @RequestLine("POST /category/group/add")
  @Headers("Content-Type: application/json")
  Mono<Result<CategoryGroupDto>> add(CategoryGroupDto categoryGroupDto);

  @RequestLine("PUT /category/group/edit")
  @Headers("Content-Type: application/json")
  Mono<Result<CategoryGroupDto>> edit(CategoryGroupDto categoryGroupDto);

  @RequestLine("PUT /category/group/delete")
  @Headers("Content-Type: application/json")
  Mono<Result<CategoryGroupDto>> delete(IdRequest idRequest);
}
