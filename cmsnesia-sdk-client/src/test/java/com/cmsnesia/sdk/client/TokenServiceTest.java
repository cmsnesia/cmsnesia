package com.cmsnesia.sdk.client;

import com.cmsnesia.model.request.TokenRequest;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.reactive.ReactorFeign;
import org.junit.Test;

public class TokenServiceTest {

    @Test
    public void requestAndRefresh() {
        TokenService tokenService = ReactorFeign.builder()
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder())
                .target(TokenService.class, "http://20.185.12.50:8080");
        System.out.println(
        tokenService.request(new TokenRequest("ardikars", "123456"))
                .block()
        );
    }

}
