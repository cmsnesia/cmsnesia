package com.cmsnesia.sdk.client;

import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.NameRequest;
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
  public void getCategoryTest() {
    CategoryService categoryService =
        ReactorFeign.builder()
            .decoder(new JacksonDecoder())
            .encoder(new JacksonEncoder())
            .requestInterceptor(interceptor())
            .target(CategoryService.class, "http://52.188.40.20:8080");
    PageResponse<CategoryDto> categories = categoryService.find(new CategoryDto(), 0, 20).block();
    System.out.println(categories);
  }

  @Test
  public void addCategoryTest() {
    CategoryService categoryService =
        ReactorFeign.builder()
            .decoder(new JacksonDecoder())
            .encoder(new JacksonEncoder())
            .requestInterceptor(interceptor())
            .target(CategoryService.class, "http://52.188.40.20:8080");
    System.out.println(
        categoryService.add(NameRequest.builder().name("MOTIVATION").build()).block());
  }

  @Test
  public void editCategoryTest() {
    CategoryService categoryService =
        ReactorFeign.builder()
            .decoder(new JacksonDecoder())
            .encoder(new JacksonEncoder())
            .requestInterceptor(interceptor())
            .target(CategoryService.class, "http://52.188.40.20:8080");
    System.out.println(
        categoryService
            .edit(
                CategoryDto.builder()
                    .id("7c26d3f9-3418-4131-9cda-29bdae154c29")
                    .name("LIFE MOTIVATION")
                    .build())
            .block());
  }

  @Test
  public void deleteCategoryTest() {
    CategoryService categoryService =
        ReactorFeign.builder()
            .decoder(new JacksonDecoder())
            .encoder(new JacksonEncoder())
            .requestInterceptor(interceptor())
            .target(CategoryService.class, "http://52.188.40.20:8080");
    System.out.println(
        categoryService
            .delete(IdRequest.builder().id("7c26d3f9-3418-4131-9cda-29bdae154c29").build())
            .block());
  }

  public RequestInterceptor interceptor() {
    return new RequestInterceptor() {
      @Override
      public void apply(RequestTemplate requestTemplate) {
        TokenService tokenService =
            ReactorFeign.builder()
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder())
                .target(TokenService.class, "http://52.188.40.20:8080");
        TokenResponse tokenResponse =
            tokenService.request(new TokenRequest("ardikars", "123456")).block();
        System.out.println(tokenResponse.getAccessToken());
        requestTemplate.header("Authorization", "Bearer " + tokenResponse.getAccessToken());
      }
    };
  }
}
