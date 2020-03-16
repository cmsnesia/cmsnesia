package com.cmsnesia.web.controller;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.request.RefreshTokenRequest;
import com.cmsnesia.model.request.TokenRequest;
import com.cmsnesia.model.response.TokenResponse;
import com.cmsnesia.service.AuthService;
import com.cmsnesia.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Collections;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

import java.util.List;

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
  private final ObjectMapper objectMapper;

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
  public Mono<ResponseEntity<?>> validate(ServerHttpRequest serverRequest) {
    String path = serverRequest.getPath().toString();
    if (path.startsWith("/token/") && path.startsWith("/public/") && !path.equals("/token/validate")) {
      return Mono.just(ResponseEntity.ok().build());
    }
    List<String> tokens = serverRequest.getHeaders().get(HttpHeaders.AUTHORIZATION);
    if (!tokens.isEmpty() && tokens.get(0) != null && tokens.get(0).length() > 7) {
      String token = tokens.get(0).substring(7);
      TokenResponse tokenResponse = new TokenResponse();
      tokenResponse.setAccessToken(token);
      return tokenService
          .validate(tokenResponse)
          .defaultIfEmpty(new AuthDto())
          .map(
              authDto -> {
                if (StringUtils.hasText(authDto.getId())
                    && !Collections.isEmpty(authDto.getApplications())) {
                  try {
                    String json = objectMapper.writeValueAsString(authDto);
                    return ResponseEntity.ok().header("X-User-Data", json).build();
                  } catch (JsonProcessingException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                  }
                } else {
                  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
              });
    }
    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }
}
