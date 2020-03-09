package com.cmsnesia.web.controller;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.ChangePasswordRequest;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.QueryPageRequest;
import com.cmsnesia.service.AuthService;
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
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final PasswordEncoder passwordEncoder;

  @ApiOperation(value = "Get user by ID", response = AuthDto.class, notes = "Result<AuthDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @GetMapping("/findById")
  public Mono<Result<AuthDto>> findById(@RequestParam("id") String id) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return authService.find(session, IdRequest.builder().id(id).build());
            });
  }

  @PostMapping("/find")
  @ApiOperation(value = "List user", response = AuthDto.class, notes = "Page<AuthDto>")
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
  public Mono<Page<AuthDto>> find(
      @RequestBody AuthDto authDto,
      @PageableDefault(direction = Sort.Direction.DESC) QueryPageRequest pageable) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              return authService.find(
                  session, authDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
            });
  }

  @ApiOperation(value = "Add user", response = AuthDto.class, notes = "Result<AuthDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PostMapping("/add")
  public Mono<Result<AuthDto>> add(@RequestBody AuthDto authDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              AuthDto dto = securing(authDto);
              return authService.add(session, dto);
            });
  }

  @ApiOperation(value = "Edit user", response = AuthDto.class, notes = "Result<AuthDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/edit")
  public Mono<Result<AuthDto>> edit(@RequestBody AuthDto authDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              AuthDto dto = securing(authDto);
              return authService.edit(session, dto);
            });
  }

  @ApiOperation(value = "Soft delete user", response = AuthDto.class, notes = "Result<AuthDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/delete")
  public Mono<Result<AuthDto>> delete(@RequestBody IdRequest idRequest) {
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

  @ApiOperation(value = "Change user password", response = AuthDto.class, notes = "Result<AuthDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/changePassword")
  public Mono<Result<AuthDto>> changePassword(
      @RequestBody ChangePasswordRequest changePasswordRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              if (changePasswordRequest
                  .getNewPassword()
                  .equals(changePasswordRequest.getConfirmNewPassword())) {
                return authService.changePassword(
                    session, passwordEncoder.encode(changePasswordRequest.getNewPassword()));
              } else {
                return Mono.just(Result.build(StatusCode.SAVE_FAILED));
              }
            });
  }

  private AuthDto securing(AuthDto authDto) {
    if (!StringUtils.isEmpty(authDto.getPassword())) {
      authDto.setPassword(passwordEncoder.encode(authDto.getPassword()));
    }
    return authDto;
  }
}
