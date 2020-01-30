package com.cmsnesia.sdk.client;

import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.PostRequest;
import com.cmsnesia.sdk.client.domain.Page;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import reactor.core.publisher.Mono;

public interface PostService {

  @RequestLine("GET /post/find?page={page}&size={size}")
  @Headers("Content-Type: application/json")
  Mono<Page<PostDto>> find(
      PostDto postDto, @Param("page") Integer page, @Param("size") Integer size);

  @RequestLine("GET /post/findDraft?page={page}&size={size}")
  @Headers("Content-Type: application/json")
  Mono<Page<PostDto>> findDraft(
      PostDto postDto, @Param("page") Integer page, @Param("size") Integer size);

  @RequestLine("POST /post/add")
  @Headers("Content-Type: application/json")
  Mono<Result<PostDto>> add(PostRequest postRequest);

  @RequestLine("PUT /post/edit")
  @Headers("Content-Type: application/json")
  Mono<Result<PostDto>> edit(PostRequest postRequest);

  @RequestLine("PUT /post/delete")
  @Headers("Content-Type: application/json")
  Mono<Result<PostDto>> delete(IdRequest idRequest);

  @RequestLine("PUT /post/publish")
  @Headers("Content-Type: application/json")
  Mono<Result<PostDto>> publish(IdRequest idRequest);
}
