package com.cmsnesia.web.controller;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.MenuDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.web.util.ConstantKeys;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.UUID;

@RestController
@RequestMapping(value = "storeage")
@Api(
    value = "Storeage API",
    tags = {"Storeage API"})
@Slf4j
public class StoreageController {

  private final WebClient webClient;

  public StoreageController(
      @Value("${github.owner}") String owner,
      @Value("${github.repo}") String repo,
      @Value("${github.accessToken}") String accessToken) {
    this.webClient =
        WebClient.builder()
            .defaultHeader(ConstantKeys.AUTHORIZATION, "token " + accessToken)
            .baseUrl("https://api.github.com/repos/" + owner + "/" + repo + "/contents/")
            .build();
  }

  @ApiOperation(value = "Request image", response = MenuDto.class, notes = "Result<MediaDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  @PostMapping(
      value = "upload",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Result<Response>> image(
      @RequestBody Request media, @RequestParam("fileType") String fileType) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (AuthDto) authentication.getPrincipal())
        .flatMap(
            session -> {
              if (!StringUtils.hasText(media.getContent())) {
                return Mono.empty();
              } else {
                int beginIndex = media.getContent().indexOf(";base64,") + 8;
                if (beginIndex >= 8 && beginIndex < media.getContent().length()) {
                  String base64 = media.getContent().substring(beginIndex);
                  media.setContent(base64);
                }
              }

              String name = UUID.randomUUID().toString().concat(".").concat(fileType);

              WebClient.RequestHeadersSpec requestHeadersSpec =
                  webClient
                      .put()
                      .uri(session.getUsername() + "/" + name)
                      .accept(MediaType.APPLICATION_JSON)
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(Mono.just(media), Request.class);
              WebClient.ResponseSpec responseSpec = requestHeadersSpec.retrieve();
              return responseSpec
                  .onRawStatus(
                      value -> {
                        return value != 201;
                      },
                      clientResponse -> Mono.empty())
                  .bodyToMono(Response.class)
                  .map(mediaDto -> Result.build(mediaDto, StatusCode.SAVE_SUCCESS));
            });
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request implements Serializable {

    private String content;
    private String message;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response implements Serializable {

    private ContentDetail content;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class ContentDetail implements Serializable {

    private String name;
    private String path;
    private String sha;
    private String size;
    private String url;
    private String htmlUrl;
    private String gitUrl;
    private String downloadUrl;
    private String type;
  }
}
