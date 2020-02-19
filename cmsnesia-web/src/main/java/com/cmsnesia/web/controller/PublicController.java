package com.cmsnesia.web.controller;

import com.cmsnesia.model.*;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.QueryPageRequest;
import com.cmsnesia.service.CategoryService;
import com.cmsnesia.service.MenuService;
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
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

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
  private final MenuService menuService;

  @PostMapping(
      value = "/posts",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "List post", response = PostDto.class, notes = "Flux [PostDto]")
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
        dataType = "integer"),
    @ApiImplicitParam(
        name = ConstantKeys.APP_ID,
        paramType = "header",
        dataType = "string",
        required = true)
  })
  public Mono<Page<PostDto>> find(
      ServerRequest request,
      @RequestBody PostDto postDto,
      @PageableDefault(direction = Sort.Direction.DESC) QueryPageRequest pageable) {
    List<String> appIds = request.headers().header(ConstantKeys.APP_ID);
    AuthDto session =
        AuthDto.builder()
            .applications(
                appIds.stream()
                    .map(id -> ApplicationDto.builder().id(id).build())
                    .collect(Collectors.toSet()))
            .build();
    return postService.find(
        session, postDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
  }

  @PostMapping(
      value = "/postById",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Post detail", response = PostDto.class, notes = "Mono [PostDto]")
  @ApiImplicitParams(
      @ApiImplicitParam(
          name = ConstantKeys.APP_ID,
          paramType = "header",
          dataType = "string",
          required = true))
  public Mono<Result<PostDto>> findById(ServerRequest request, @RequestBody IdRequest id) {
    List<String> appIds = request.headers().header(ConstantKeys.APP_ID);
    AuthDto session =
        AuthDto.builder()
            .applications(
                appIds.stream()
                    .map(appId -> ApplicationDto.builder().id(appId).build())
                    .collect(Collectors.toSet()))
            .build();
    return postService.find(null, id);
  }

  @PostMapping(
      value = "/categories",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "List category", response = CategoryDto.class, notes = "Flux [CategoryDto]")
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
        dataType = "integer"),
    @ApiImplicitParam(
        name = ConstantKeys.APP_ID,
        paramType = "header",
        dataType = "string",
        required = true)
  })
  public Mono<Page<CategoryDto>> find(
      ServerRequest request,
      @RequestBody CategoryDto categoryDto,
      @PageableDefault(direction = Sort.Direction.DESC) QueryPageRequest pageable) {
    List<String> appIds = request.headers().header(ConstantKeys.APP_ID);
    AuthDto session =
        AuthDto.builder()
            .applications(
                appIds.stream()
                    .map(id -> ApplicationDto.builder().id(id).build())
                    .collect(Collectors.toSet()))
            .build();
    return categoryService.find(
        session, categoryDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
  }

  @PostMapping(
      value = "/menus",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "List menus", response = MenuDto.class, notes = "Flux [MenuDto]")
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
        dataType = "integer"),
    @ApiImplicitParam(
        name = ConstantKeys.APP_ID,
        paramType = "header",
        dataType = "string",
        required = true)
  })
  public Mono<Page<MenuDto>> find(
      ServerRequest request,
      @RequestBody MenuDto menuDto,
      @PageableDefault(direction = Sort.Direction.DESC) QueryPageRequest pageable) {
    List<String> appIds = request.headers().header(ConstantKeys.APP_ID);
    AuthDto session =
        AuthDto.builder()
            .applications(
                appIds.stream()
                    .map(id -> ApplicationDto.builder().id(id).build())
                    .collect(Collectors.toSet()))
            .build();
    return menuService.find(
        session, menuDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
  }
}
