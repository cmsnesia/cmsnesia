package com.cmsnesia.sdk.client;

import com.cmsnesia.model.*;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
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

  @RequestLine("POST /public/popularPosts?page={page}&size={size}")
  @Headers("Content-Type: application/json")
  Mono<Page<PostDto>> findPopularPosts(
      PostDto postDto, @Param("page") Integer page, @Param("size") Integer size);

  @RequestLine("POST /public/postById")
  @Headers("Content-Type: application/json")
  Mono<Result<PostDto>> findPostById(IdRequest id);

  @RequestLine("POST /public/categories?page={page}&size={size}")
  @Headers("Content-Type: application/json")
  Mono<Page<CategoryDto>> findCategories(
      CategoryDto categoryDto, @Param("page") Integer page, @Param("size") Integer size);

  @RequestLine("POST /public/menus?page={page}&size={size}")
  @Headers("Content-Type: application/json")
  Mono<Page<MenuDto>> findMenus(
      MenuDto menuDto, @Param("page") Integer page, @Param("size") Integer size);

  @RequestLine("GET /public/profile")
  @Headers("Content-Type: application/json")
  Mono<Result<ProfileDto>> findProfile();

  @RequestLine("POST /public/pageById")
  @Headers("Content-Type: application/json")
  Mono<Result<PageDto>> findPageById(IdRequest id);
}
