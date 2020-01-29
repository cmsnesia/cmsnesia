package com.cmsnesia.web.config;

import com.cmsnesia.web.config.security.SecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

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
    "/public/**",
    "/*/**"
  };

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityWebFilterChain securitygWebFilterChain(
      ServerHttpSecurity http, SecurityContextRepository contextRepository) {
    return http.csrf()
        .disable()
        .httpBasic()
        .disable()
        .formLogin()
        .disable()
        .securityContextRepository(contextRepository)
        .authorizeExchange()
        .pathMatchers(AUTH_WHITELIST)
        .permitAll()
        .anyExchange()
        .authenticated()
        .and()
        .build();
  }
}
