package com.cmsnesia.web.controller;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.model.PostDto;
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

  private final PostAssembler postAssembler;
  private final PostService postService;

  @ApiOperation(value = "Get post by ID", response = PostDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string")
  })
  @GetMapping("/findById")
  public Mono<Result<PostDto>> findByIdOrLink(
      @RequestParam("id") String id, @RequestParam(value = "link", required = false) String link) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.findByIdOrLink(
                  session, PostDto.builder().id(id).link(link).build());
            });
  }

  @ApiOperation(value = "Get draft by ID", response = PostDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string")
  })
  @GetMapping("/findDraftById")
  public Mono<Result<PostDto>> findDraftById(@RequestParam("id") String id) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.findDraft(session, IdRequest.builder().id(id).build());
            });
  }

  @PostMapping("/find")
  @ApiOperation(value = "List post", response = PostDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string"),
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
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.find(
                  session, postDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
            });
  }

  @PostMapping("/findDraft")
  @ApiOperation(value = "List draft", response = PostDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string"),
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
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.findDraft(
                  session, postDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
            });
  }

  @ApiOperation(value = "Add post", response = PostDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string")
  })
  @PostMapping("/add")
  public Mono<Result<PostDto>> add(@RequestBody PostRequest postRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              PostDto postDto = postAssembler.fromRequest(postRequest);
              return postService.add(session, postDto);
            });
  }

  @ApiOperation(value = "Edit post", response = PostDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string")
  })
  @PutMapping("/edit")
  public Mono<Result<PostDto>> edit(@RequestBody IdRequest idRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.edit(session, PostDto.builder().id(idRequest.getId()).build());
            });
  }

  @ApiOperation(value = "Edit draft", response = PostDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string")
  })
  @PutMapping("/editDraft")
  public Mono<Result<PostDto>> editDraft(@RequestBody PostEditRequest postEditRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              PostDto postDto = postAssembler.fromEditRequest(postEditRequest);
              return postService.editDraft(session, postDto);
            });
  }

  @ApiOperation(value = "Soft delete post", response = PostDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string")
  })
  @PutMapping("/delete")
  public Mono<Result<PostDto>> delete(@RequestBody IdRequest idRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              PostDto dto = new PostDto();
              dto.setId(idRequest.getId());
              return postService.delete(session, dto);
            });
  }

  @ApiOperation(value = "Delete draft", response = PostDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string")
  })
  @PutMapping("/deleteDraft")
  public Mono<Result<PostDto>> deleteDraft(@RequestBody IdRequest idRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              PostDto dto = new PostDto();
              dto.setId(idRequest.getId());
              return postService.deleteDraft(session, dto);
            });
  }

  @ApiOperation(value = "Publish post", response = PostDto.class, notes = "Result<PostDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string")
  })
  @PutMapping("/publish")
  public Mono<Result<PostDto>> publish(@RequestBody IdRequest idRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return postService.publish(session, idRequest);
            });
  }
}
