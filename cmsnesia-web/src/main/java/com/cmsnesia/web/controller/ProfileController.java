package com.cmsnesia.web.controller;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.model.ProfileDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.service.ProfileService;
import com.cmsnesia.web.util.ConstantKeys;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "profile")
@Api(
    value = "Profile API",
    tags = {"Profile"})
@Slf4j
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService profileService;

  @ApiOperation(
      value = "Get profile by ID",
      response = ProfileDto.class,
      notes = "Result<ProfileDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @GetMapping("/findById")
  public Mono<Result<ProfileDto>> findById() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(session -> profileService.find(session, null));
  }

  @ApiOperation(value = "Add Profile", response = ProfileDto.class, notes = "Result<ProfileDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PostMapping("/add")
  public Mono<Result<ProfileDto>> add(@RequestBody ProfileDto profileDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(session -> profileService.add(session, profileDto));
  }

  @ApiOperation(value = "Edit profile", response = ProfileDto.class, notes = "Result<ProfileDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PutMapping("/edit")
  public Mono<Result<ProfileDto>> edit(@RequestBody ProfileDto profileDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(session -> profileService.edit(session, profileDto));
  }
}
