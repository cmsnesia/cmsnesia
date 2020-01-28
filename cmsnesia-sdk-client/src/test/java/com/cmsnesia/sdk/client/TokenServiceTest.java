package com.cmsnesia.sdk.client;

import com.cmsnesia.model.request.RefreshTokenRequest;
import com.cmsnesia.model.request.TokenRequest;
import com.cmsnesia.model.response.TokenResponse;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.reactive.ReactorFeign;
import org.junit.Test;

public class TokenServiceTest {

  @Test
  public void requestAndRefresh() {
    TokenService tokenService =
        ReactorFeign.builder()
            .decoder(new JacksonDecoder())
            .encoder(new JacksonEncoder())
            .target(TokenService.class, "http://20.185.12.50:8080");
    TokenResponse tokenResponse =
        tokenService.request(new TokenRequest("ardikars", "123456")).block();
    //        System.out.println(tokenResponse);
    TokenResponse tokenResponseRefresed =
        tokenService.refresh(new RefreshTokenRequest(tokenResponse.getRefreshToken())).block();
    System.out.println(tokenResponseRefresed);
  }
}
