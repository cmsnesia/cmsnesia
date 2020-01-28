package com.cmsnesia.sdk.client;

import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.response.PageResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import reactor.core.publisher.Mono;

public interface CategoryService {

  @RequestLine("GET /category/find?page={page}&size={size}")
  @Headers("Content-Type: application/json")
  Mono<PageResponse<CategoryDto>> find(
      CategoryDto dto, @Param("page") Integer page, @Param("size") Integer size);
}
