package com.cmsnesia.web.controller;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.TagDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.PostEditRequest;
import com.cmsnesia.model.request.PostRequest;
import com.cmsnesia.model.request.QueryPageRequest;
import com.cmsnesia.service.PostService;
import com.cmsnesia.web.util.ConstantKeys;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "post")
@Api(
    value = "Post API",
    tags = {"Post"})
@Slf4j
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  @ApiOperation(value = "Get post by ID", response = AuthDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @GetMapping("/findById")
  public Mono<Result<PostDto>> findById(@RequestParam("id") String id) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.find(session, IdRequest.builder().id(id).build());
            });
  }

  @ApiOperation(value = "Get draft by ID", response = AuthDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @GetMapping("/findDraftById")
  public Mono<Result<PostDto>> findDraftById(@RequestParam("id") String id) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.findDraft(session, IdRequest.builder().id(id).build());
            });
  }

  @PostMapping("/find")
  @ApiOperation(value = "List post", response = AuthDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string"),
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
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.find(
                  session, postDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
            });
  }

  @PostMapping("/findDraft")
  @ApiOperation(value = "List draft", response = AuthDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string"),
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
  public Mono<Page<PostDto>> findDraft(
      @RequestBody PostDto postDto,
      @PageableDefault(direction = Sort.Direction.DESC) QueryPageRequest pageable) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.findDraft(
                  session, postDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
            });
  }

  @ApiOperation(value = "Add post", response = AuthDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PostMapping("/add")
  public Mono<Result<PostDto>> add(@RequestBody PostRequest postRequest) {
    PostDto postDto =
        PostDto.builder()
            .title(postRequest.getTitle())
            .content(postRequest.getContent())
            .medias(postRequest.getMedias())
            .tags(
                postRequest.getTags().stream()
                    .map(tag -> TagDto.builder().name(tag.getName()).build())
                    .collect(Collectors.toSet()))
            .categories(
                postRequest.getCategories().stream()
                    .map(id -> CategoryDto.builder().id(id.getId()).build())
                    .collect(Collectors.toSet()))
            .build();
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.add(session, postDto);
            });
  }

  @ApiOperation(value = "Edit post", response = AuthDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/edit")
  public Mono<Result<PostDto>> edit(@RequestBody PostEditRequest postEditRequest) {
    PostDto postDto =
        PostDto.builder()
            .id(postEditRequest.getId())
            .title(postEditRequest.getTitle())
            .content(postEditRequest.getContent())
            .medias(postEditRequest.getMedias())
            .tags(
                postEditRequest.getTags().stream()
                    .map(tagDto -> TagDto.builder().name(tagDto.getName()).build())
                    .collect(Collectors.toSet()))
            .categories(
                postEditRequest.getCategories().stream()
                    .map(id -> CategoryDto.builder().id(id.getId()).build())
                    .collect(Collectors.toSet()))
            .build();
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.edit(session, postDto);
            });
  }

  @ApiOperation(value = "Edit draft", response = AuthDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/editDraft")
  public Mono<Result<PostDto>> editDraft(@RequestBody PostEditRequest postEditRequest) {
    PostDto postDto =
        PostDto.builder()
            .id(postEditRequest.getId())
            .title(postEditRequest.getTitle())
            .content(postEditRequest.getContent())
            .medias(postEditRequest.getMedias())
            .tags(
                postEditRequest.getTags().stream()
                    .map(tagDto -> TagDto.builder().name(tagDto.getName()).build())
                    .collect(Collectors.toSet()))
            .categories(
                postEditRequest.getCategories().stream()
                    .map(id -> CategoryDto.builder().id(id.getId()).build())
                    .collect(Collectors.toSet()))
            .build();
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.editDraft(session, postDto);
            });
  }

  @ApiOperation(value = "Soft delete post", response = AuthDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/delete")
  public Mono<Result<PostDto>> delete(@RequestBody IdRequest idRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              PostDto dto = new PostDto();
              dto.setId(idRequest.getId());
              return postService.delete(session, dto);
            });
  }

  @ApiOperation(value = "Delete draft", response = AuthDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/deleteDraft")
  public Mono<Result<PostDto>> deleteDraft(@RequestBody IdRequest idRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              PostDto dto = new PostDto();
              dto.setId(idRequest.getId());
              return postService.deleteDraft(session, dto);
            });
  }

  @ApiOperation(value = "Publish post", response = AuthDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/publish")
  public Mono<Result<PostDto>> publish(@RequestBody IdRequest idRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.publish(session, idRequest);
            });
  }
}
