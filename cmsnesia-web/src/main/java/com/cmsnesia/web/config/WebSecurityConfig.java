package com.cmsnesia.web.config;

import com.cmsnesia.web.config.security.SecurityContextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

  private static final String[] AUTH_WHITELIST = {
    "/v2/api-docs",
    "/resources/**",
    "/configuration/**",
    "/swagger*/**",
    "/webjars/**",
    "/token/**",
    "/favicon.ico",
    "/public/**"
    //    "/*/**"
  };

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityWebFilterChain securitygWebFilterChain(
      ServerHttpSecurity http, SecurityContextRepository contextRepository) {
      Logger logger = LoggerFactory.getLogger(AuthenticationEntryPoint.class);
    return http.exceptionHandling()
        .authenticationEntryPoint(
            (swe, e) ->
                Mono.fromRunnable(
                    () -> {
                    try {
                      ServerHttpRequest request = swe.getRequest();
                      logger.info("ID      : {}", request.getPath());
                      logger.info("Path    : {}", request.getId());
                      logger.info("URI     : {}", request.getURI().toString());
                      logger.info("URL     : {}", request.getURI().toURL().toString());
                      logger.info("Method  : {}", request.getMethod());
                      logger.info("Headers :");
                      request.getHeaders().forEach((name, httpHeaders) -> {
                        logger.info(" >> header = {} : {}", name, httpHeaders);
                      });
                      logger.info("Cookies : ");
                      request.getCookies().forEach((name, httpCookies) -> {
                        logger.info(" >> cookie = {} : {}", name, httpCookies);
                      });
                    } catch (MalformedURLException ex) {
                    }
                    swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    }))
        .accessDeniedHandler(
            (swe, e) ->
                Mono.fromRunnable(
                    () -> {
                      swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    }))
        .and()
        .csrf()
        .disable()
        .formLogin()
        .disable()
        .securityContextRepository(contextRepository)
        .authorizeExchange()
        .pathMatchers(AUTH_WHITELIST)
        .permitAll()
        .pathMatchers(HttpMethod.OPTIONS)
        .permitAll()
        .anyExchange()
        .authenticated()
        .and()
        .build();
  }
}
