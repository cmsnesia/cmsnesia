package com.cmsnesia.sdk.client;

import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.sdk.client.domain.Page;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import reactor.core.publisher.Mono;

public interface PublicService {

  @RequestLine("POST /public/posts?page={page}&size={size}")
  @Headers("Content-Type: application/json")
  Mono<Page<PostDto>> findPosts(
      PostDto postDto, @Param("page") Integer page, @Param("size") Integer size);

  @RequestLine("POST /public/categories?page={page}&size={size}")
  @Headers("Content-Type: application/json")
  Mono<Page<CategoryDto>> findCategories(
      CategoryDto categoryDto, @Param("page") Integer page, @Param("size") Integer size);

}
