package com.cmsnesia.web.controller;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.model.MenuDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.QueryPageRequest;
import com.cmsnesia.service.MenuService;
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
@RequestMapping(value = "menu")
@Api(
    value = "Menu API",
    tags = {"Menu"})
@Slf4j
@RequiredArgsConstructor
public class MenuController {

  private final MenuService menuService;

  @ApiOperation(value = "Get menu by ID", response = MenuDto.class, notes = "Result<MenuDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @GetMapping("/findById")
  public Mono<Result<MenuDto>> findById(@RequestParam("id") String id) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return menuService.find(session, IdRequest.builder().id(id).build());
            });
  }

  @PostMapping("/find")
  @ApiOperation(value = "List menu", response = MenuDto.class, notes = "Page<MenuDto>")
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
  public Mono<Page<MenuDto>> find(
      @RequestBody MenuDto menuDto,
      @PageableDefault(direction = Sort.Direction.DESC) QueryPageRequest pageable) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return menuService.find(
                  session, menuDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
            });
  }

  @ApiOperation(value = "Add Menu", response = MenuDto.class, notes = "Result<MenuDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PostMapping("/add")
  public Mono<Result<MenuDto>> add(@RequestBody MenuDto menuDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return menuService.add(session, menuDto);
            });
  }

  @ApiOperation(value = "Edit menu", response = MenuDto.class, notes = "Result<MenuDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/edit")
  public Mono<Result<MenuDto>> edit(@RequestBody MenuDto menuDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return menuService.edit(session, menuDto);
            });
  }

  @ApiOperation(value = "Soft delete menu", response = MenuDto.class, notes = "Result<MenuDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/delete")
  public Mono<Result<MenuDto>> delete(@RequestBody IdRequest idRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              MenuDto dto = new MenuDto();
              dto.setId(idRequest.getId());
              return menuService.delete(session, dto);
            });
  }
}
