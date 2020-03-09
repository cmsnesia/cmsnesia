package com.cmsnesia.web.controller;

import com.cmsnesia.model.request.RefreshTokenRequest;
import com.cmsnesia.model.request.TokenRequest;
import com.cmsnesia.model.response.TokenResponse;
import com.cmsnesia.service.AuthService;
import com.cmsnesia.service.TokenService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "token")
@Api(
    value = "Token API",
    tags = {"Token"})
@Slf4j
@RequiredArgsConstructor
public class TokenController {

  private final AuthService authService;
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/request")
  public Mono<ResponseEntity<?>> request(@RequestBody TokenRequest request) {
    return authService
        .findByUsername(request.getUsername())
        .map(
            (userDetails) -> {
              if (passwordEncoder.matches(
                  request.getPasssword(), userDetails.getData().getPassword())) {
                return ResponseEntity.ok(tokenService.request(request));
              } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
              }
            })
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }

  @PutMapping("/refresh")
  public Mono<ResponseEntity<?>> refresh(@RequestBody RefreshTokenRequest request) {
    return Mono.just(ResponseEntity.ok(tokenService.refresh(request)));
  }

  @PutMapping("/destroy")
  public Mono<ResponseEntity<?>> destroy(@RequestBody TokenResponse tokenResponse) {
    return Mono.just(ResponseEntity.ok(tokenService.destroy(tokenResponse)));
  }

  @PostMapping("/validate")
  public Mono<ResponseEntity<?>> validate(TokenResponse tokenResponse) {
    return Mono.just(ResponseEntity.ok(tokenService.validate(tokenResponse)));
  }
}
