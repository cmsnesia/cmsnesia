package com.cmsnesia.web.controller;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryGroupDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.NameRequest;
import com.cmsnesia.model.request.QueryPageRequest;
import com.cmsnesia.service.CategoryGroupService;
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
@RequestMapping(value = "category/group")
@Api(
    value = "Category Group API",
    tags = {"Category Group "})
@Slf4j
@RequiredArgsConstructor
public class CategoryGroupController {

  private final CategoryGroupService categoryGroupService;

  @ApiOperation(
      value = "Get category group by ID",
      response = CategoryGroupDto.class,
      notes = "Result<CategoryGroupDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @GetMapping("/findById")
  public Mono<Result<CategoryGroupDto>> findById(@RequestParam("id") String id) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return categoryGroupService.find(session, IdRequest.builder().id(id).build());
            });
  }

  @PostMapping("/find")
  @ApiOperation(value = "List category group", response = CategoryGroupDto.class, notes = "Page<CategoryGroupDto>")
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
  public Mono<Page<CategoryGroupDto>> find(
      @RequestBody CategoryGroupDto categoryGroupDto,
      @PageableDefault(direction = Sort.Direction.DESC) QueryPageRequest pageable) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return categoryGroupService.find(
                  session, categoryGroupDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
            });
  }

  @ApiOperation(value = "Add category group", response = CategoryGroupDto.class, notes = "Result<CategoryGroupDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PostMapping("/add")
  public Mono<Result<CategoryGroupDto>> add(@RequestBody CategoryGroupDto categoryGroupDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return categoryGroupService.add(session, categoryGroupDto);
            });
  }

  @ApiOperation(value = "Edit category group", response = CategoryGroupDto.class, notes = "Result<CategoryGroupDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/edit")
  public Mono<Result<CategoryGroupDto>> edit(@RequestBody CategoryGroupDto categoryGroupDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return categoryGroupService.edit(session, categoryGroupDto);
            });
  }

  @ApiOperation(
      value = "Soft delete category group",
      response = CategoryGroupDto.class,
      notes = "Result<CategoryGroupDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/delete")
  public Mono<Result<CategoryGroupDto>> delete(@RequestBody IdRequest idRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              CategoryGroupDto dto = new CategoryGroupDto();
              dto.setId(idRequest.getId());
              return categoryGroupService.delete(session, dto);
            });
  }
}
