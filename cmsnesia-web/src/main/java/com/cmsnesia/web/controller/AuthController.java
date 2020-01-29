package com.cmsnesia.web.controller;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.response.PageResponse;
import com.cmsnesia.service.AuthService;
import com.cmsnesia.web.util.ConstantKeys;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "auth")
@Api(
    value = "Auth API",
    tags = {"Auth"})
@Slf4j
public class AuthController {

  private final AuthService authService;
  private final PasswordEncoder passwordEncoder;

  public AuthController(AuthService authService, PasswordEncoder passwordEncoder) {
    this.authService = authService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/find")
  @ApiOperation(value = "List user", response = AuthDto.class, notes = "Flux [AuthDto]")
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
  public Mono<PageResponse<AuthDto>> find(
      @RequestBody AuthDto authDto,
      @RequestParam("page") Integer page,
      @RequestParam("size") Integer size) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return authService.find(session, authDto, PageRequest.of(page, size));
            });
  }

  @ApiOperation(value = "Add user", response = AuthDto.class, notes = "Mono [AuthDto]")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PostMapping("/add")
  public Mono<AuthDto> add(@RequestBody AuthDto authDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              AuthDto dto = securing(authDto);
              return authService.add(session, dto);
            });
  }

  @ApiOperation(value = "Edit user", response = AuthDto.class, notes = "Mono [AuthDto]")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/edit")
  public Mono<AuthDto> edit(@RequestBody AuthDto authDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              AuthDto dto = securing(authDto);
              return authService.edit(session, dto);
            });
  }

  @ApiOperation(value = "Soft delete user", response = AuthDto.class, notes = "Mono [AuthDto]")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/delete")
  public Mono<AuthDto> delete(@RequestBody IdRequest idRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              AuthDto dto = new AuthDto();
              dto.setId(idRequest.getId());
              return authService.delete(session, dto);
            });
  }

  private AuthDto securing(AuthDto authDto) {
    if (!StringUtils.isEmpty(authDto.getPassword())) {
      authDto.setPassword(passwordEncoder.encode(authDto.getPassword()));
    }
    return authDto;
  }
}
