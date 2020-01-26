package com.cmsnesia.sdk.client;

import feign.reactive.ReactorFeign;
import org.junit.Test;

public class TokenServiceTest {

    @Test
    public void requestAndRefresh() {
        TokenService tokenService = ReactorFeign.builder()
                .target(TokenService.class, "https://api.github.com");

    }

}
