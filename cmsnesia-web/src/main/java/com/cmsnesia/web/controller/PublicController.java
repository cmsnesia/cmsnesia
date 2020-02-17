package com.cmsnesia.web.controller;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.QueryPageRequest;
import com.cmsnesia.service.CategoryService;
import com.cmsnesia.service.PostService;
import com.cmsnesia.web.util.ConstantKeys;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "public")
@Api(
    value = "Public API",
    tags = {"Public API"})
@Slf4j
@RequiredArgsConstructor
public class PublicController {

  private final CategoryService categoryService;
  private final PostService postService;

  @PostMapping(
      value = "/posts",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "List post", response = AuthDto.class, notes = "Flux [PostDto]")
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = ConstantKeys.PAGE,
        defaultValue = "0",
        paramType = "query",
        dataType = "integer"),
    @ApiImplicitParam(
        name = ConstantKeys.SIZE,
        defaultValue = "10",
        paramType = "query",
        dataType = "integer")
  })
  public Mono<Page<PostDto>> find(
      @RequestBody PostDto postDto,
      @PageableDefault(direction = Sort.Direction.DESC) QueryPageRequest pageable) {
    return postService.find(null, postDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
  }

  @PostMapping(
      value = "/postById",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Post detail", response = AuthDto.class, notes = "Mono [PostDto]")
  public Mono<Result<PostDto>> findById(@RequestBody IdRequest id) {
    return postService.find(null, id);
  }

  @PostMapping(
      value = "/categories",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "List category", response = AuthDto.class, notes = "Flux [CategoryDto]")
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = ConstantKeys.PAGE,
        defaultValue = "0",
        paramType = "query",
        dataType = "integer"),
    @ApiImplicitParam(
        name = ConstantKeys.SIZE,
        defaultValue = "10",
        paramType = "query",
        dataType = "integer")
  })
  public Mono<Page<CategoryDto>> find(
      @RequestBody CategoryDto categoryDto,
      @PageableDefault(direction = Sort.Direction.DESC) QueryPageRequest pageable) {
    return categoryService.find(
        null, categoryDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
  }
}
