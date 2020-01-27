package com.cmsnesia.web.controller;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.TagDto;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.PageRequest;
import com.cmsnesia.model.request.PostEditRequest;
import com.cmsnesia.model.request.PostRequest;
import com.cmsnesia.service.PostService;
import com.cmsnesia.web.util.ConstantKeys;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "post")
@Api(value = "Post API", tags = {"Post"})
@Slf4j
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/find")
    @ApiOperation(
            value = "List post",
            response = AuthDto.class,
            notes = "Flux [PostDto]")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = ConstantKeys.AUTHORIZATION,
                    paramType = "header",
                    dataType = "string"),
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
    public Mono<Page<PostDto>> find(@RequestBody PostDto postDto, PageRequest pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMap(session -> {
                    return postService.find(session, postDto, org.springframework.data.domain.PageRequest.of(pageable.getPage(), pageable.getSize()));
                });
    }

    @PostMapping("/findDraft")
    @ApiOperation(
            value = "List draft",
            response = AuthDto.class,
            notes = "Flux [PostDto]")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = ConstantKeys.AUTHORIZATION,
                    paramType = "header",
                    dataType = "string"),
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
    public Mono<Page<PostDto>> findDraft(@RequestBody PostDto postDto, PageRequest pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMap(session -> {
                    return postService.findDraft(session, postDto, org.springframework.data.domain.PageRequest.of(pageable.getPage(), pageable.getSize()));
                });
    }

    @ApiOperation(
            value = "Add post",
            response = AuthDto.class,
            notes = "Mono [PostDto]")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = ConstantKeys.AUTHORIZATION,
                    paramType = "header",
                    dataType = "string")
    })
    @PostMapping("/add")
    public Mono<PostDto> add(@RequestBody PostRequest postRequest) {
        PostDto postDto = PostDto.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .medias(postRequest.getMedias())
                .tags(postRequest.getTags().stream()
                        .map(tag -> TagDto.builder()
                                .name(tag.getName())
                                .build())
                        .collect(Collectors.toSet()))
                .categories(postRequest.getCategories().stream()
                        .map(id -> CategoryDto.builder().id(id.getId()).build())
                        .collect(Collectors.toSet()))
                .build();
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMap(session -> {
                    return postService.add(session, postDto);
                });
    }

    @ApiOperation(
            value = "Edit post",
            response = AuthDto.class,
            notes = "Mono [PostDto]")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = ConstantKeys.AUTHORIZATION,
                    paramType = "header",
                    dataType = "string")
    })
    @PutMapping("/edit")
    public Mono<PostDto> edit(@RequestBody PostEditRequest postEditRequest) {
        PostDto postDto = PostDto.builder()
                .id(postEditRequest.getId())
                .title(postEditRequest.getTitle())
                .content(postEditRequest.getContent())
                .medias(postEditRequest.getMedias())
                .tags(postEditRequest.getTags().stream()
                        .map(tagDto -> TagDto.builder().name(tagDto.getName()).build())
                        .collect(Collectors.toSet()))
                .categories(postEditRequest.getCategories().stream()
                        .map(id -> CategoryDto.builder().id(id.getId()).build())
                        .collect(Collectors.toSet()))
                .build();
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMap(session -> {
                    return postService.edit(session, postDto);
                });
    }

    @ApiOperation(
            value = "Soft delete post",
            response = AuthDto.class,
            notes = "Mono [PostDto]")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = ConstantKeys.AUTHORIZATION,
                    paramType = "header",
                    dataType = "string")
    })
    @PutMapping("/delete")
    public Mono<PostDto> delete(@RequestBody IdRequest idRequest) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMap(session -> {
                    PostDto dto = new PostDto();
                    dto.setId(idRequest.getId());
                    return postService.delete(session, dto);
                });
    }

    @ApiOperation(
            value = "Publish post",
            response = AuthDto.class,
            notes = "Mono [PostDto]")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = ConstantKeys.AUTHORIZATION,
                    paramType = "header",
                    dataType = "string")
    })
    @PutMapping("/publish")
    public Mono<PostDto> publish(@RequestBody IdRequest idRequest) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMap(session -> {
                    return postService.publish(session, idRequest);
                });
    }

}
