package com.cmsnesia.web.controller;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.model.PageDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.QueryPageRequest;
import com.cmsnesia.service.PageService;
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
@RequestMapping(value = "page")
@Api(
    value = "Page API",
    tags = {"Page"})
@Slf4j
@RequiredArgsConstructor
public class PageController {

  private final PageService pageService;

  @ApiOperation(value = "Get page by ID", response = PageDto.class, notes = "Result<PageDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @GetMapping("/findById")
  public Mono<Result<PageDto>> findById(@RequestParam("id") String id) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return pageService.find(session, IdRequest.builder().id(id).build());
            });
  }

  @PostMapping("/find")
  @ApiOperation(value = "List page", response = PageDto.class, notes = "Page<PageDto>")
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
  public Mono<Page<PageDto>> find(
      @RequestBody PageDto pageDto,
      @PageableDefault(direction = Sort.Direction.DESC) QueryPageRequest pageable) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return pageService.find(
                  session, pageDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
            });
  }

  @ApiOperation(value = "Add Page", response = PageDto.class, notes = "Result<PageDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PostMapping("/add")
  public Mono<Result<PageDto>> add(@RequestBody PageDto pageDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return pageService.add(session, pageDto);
            });
  }

  @ApiOperation(value = "Edit page", response = PageDto.class, notes = "Result<PageDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/edit")
  public Mono<Result<PageDto>> edit(@RequestBody PageDto pageDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return pageService.edit(session, pageDto);
            });
  }

  @ApiOperation(value = "Soft delete page", response = PageDto.class, notes = "Result<PageDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/delete")
  public Mono<Result<PageDto>> delete(@RequestBody IdRequest idRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              PageDto dto = new PageDto();
              dto.setId(idRequest.getId());
              return pageService.delete(session, dto);
            });
  }
}
