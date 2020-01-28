package com.cmsnesia.sdk.client;

import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.NameRequest;
import com.cmsnesia.model.response.PageResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import reactor.core.publisher.Mono;

public interface CategoryService {

  @RequestLine("GET /category/find?page={page}&size={size}")
  @Headers("Content-Type: application/json")
  Mono<PageResponse<CategoryDto>> find(
      CategoryDto categoryDto, @Param("page") Integer page, @Param("size") Integer size);

  @RequestLine("POST /category/add")
  @Headers("Content-Type: application/json")
  Mono<CategoryDto> add(NameRequest nameRequest);

  @RequestLine("PUT /category/edit")
  @Headers("Content-Type: application/json")
  Mono<CategoryDto> edit(CategoryDto categoryDto);

  @RequestLine("PUT /category/delete")
  @Headers("Content-Type: application/json")
  Mono<CategoryDto> delete(IdRequest idRequest);
}
