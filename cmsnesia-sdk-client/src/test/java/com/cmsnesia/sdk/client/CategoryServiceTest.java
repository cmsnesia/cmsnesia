package com.cmsnesia.sdk.client;

import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.request.TokenRequest;
import com.cmsnesia.model.response.PageResponse;
import com.cmsnesia.model.response.TokenResponse;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.reactive.ReactorFeign;
import org.junit.Test;

public class CategoryServiceTest {

  @Test
  public void categoryTest() {
    CategoryService categoryService =
        ReactorFeign.builder()
            .decoder(new JacksonDecoder())
            .encoder(new JacksonEncoder())
            .requestInterceptor(interceptor())
            .target(CategoryService.class, "http://localhost:8080");
    PageResponse<CategoryDto> categories = categoryService.find(new CategoryDto(), 0, 20).block();
    categories.getContent().forEach(System.out::println);
  }

  public RequestInterceptor interceptor() {
    return new RequestInterceptor() {
      @Override
      public void apply(RequestTemplate requestTemplate) {
        TokenService tokenService =
            ReactorFeign.builder()
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder())
                .target(TokenService.class, "http://localhost:8080");
        TokenResponse tokenResponse =
            tokenService.request(new TokenRequest("ardikars", "123456")).block();
        System.out.println(tokenResponse.getAccessToken());
        requestTemplate.header("Authorization", "Bearer " + tokenResponse.getAccessToken());
      }
    };
  }
}
