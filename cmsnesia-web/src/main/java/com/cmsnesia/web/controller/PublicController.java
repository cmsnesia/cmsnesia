package com.cmsnesia.web.controller;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.response.PageResponse;
import com.cmsnesia.service.CategoryService;
import com.cmsnesia.service.PostService;
import com.cmsnesia.web.util.ConstantKeys;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "public")
@Api(
    value = "Public API",
    tags = {"Public API"})
@Slf4j
public class PublicController {

  private final CategoryService categoryService;
  private final PostService postService;

  public PublicController(CategoryService categoryService, PostService postService) {
    this.categoryService = categoryService;
    this.postService = postService;
  }

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
  public Mono<PageResponse<PostDto>> find(
      @RequestBody PostDto postDto,
      @RequestParam("page") Integer page,
      @RequestParam("size") Integer size) {
    return postService.find(null, postDto, PageRequest.of(page, size));
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
  public Mono<PageResponse<CategoryDto>> find(
      @RequestBody CategoryDto categoryDto,
      @RequestParam("page") Integer page,
      @RequestParam("size") Integer size) {
    return categoryService.find(null, categoryDto, PageRequest.of(page, size));
  }
}
