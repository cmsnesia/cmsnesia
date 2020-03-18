package com.cmsnesia.web.config.security;

import com.cmsnesia.accounts.model.Session;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

  private final ObjectMapper objectMapper;

  @Override
  public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Mono<SecurityContext> load(ServerWebExchange exchange) {
    ServerHttpRequest request = exchange.getRequest();
    String userDataJson = request.getHeaders().getFirst("X-User-Data");
    if (StringUtils.hasText(userDataJson)) {
      try {
        Session session =
            objectMapper.readValue(
                new String(Base64.getDecoder().decode(userDataJson)), Session.class);
        SecurityContext securityContext =
            new SecurityContextImpl(
                new Authentication() {
                  @Override
                  public Collection<? extends GrantedAuthority> getAuthorities() {
                    return session.getRoles() == null
                        ? new HashSet<>()
                        : session.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority(role))
                            .collect(Collectors.toSet());
                  }

                  @Override
                  public Object getCredentials() {
                    return session.getPassword();
                  }

                  @Override
                  public Object getDetails() {
                    return session;
                  }

                  @Override
                  public Object getPrincipal() {
                    return session;
                  }

                  @Override
                  public boolean isAuthenticated() {
                    return true;
                  }

                  @Override
                  public void setAuthenticated(boolean authenticated)
                      throws IllegalArgumentException {}

                  @Override
                  public String getName() {
                    return session.getFullName();
                  }
                });
        return Mono.just(securityContext);
      } catch (JsonProcessingException e) {
        return Mono.empty();
      }
    }
    return Mono.empty();
  }
}
