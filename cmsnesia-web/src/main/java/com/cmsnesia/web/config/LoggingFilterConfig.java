package com.cmsnesia.web.config;

import com.cmsnesia.web.config.interceptor.RequestLoggingInterceptor;
import com.cmsnesia.web.config.interceptor.ResponseLoggingInterceptor;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class LoggingFilterConfig implements WebFilter {

  private final String ignorePatterns = "/swagger";
  private final boolean logHeaders = true;
  private final boolean useContentLength = true;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    if (ignorePatterns != null
        && exchange.getRequest().getURI().getPath().matches(ignorePatterns)) {
      return chain.filter(exchange);
    } else {
      final long startTime = System.currentTimeMillis();
      List<String> header = exchange.getRequest().getHeaders().get("Content-Length");
      if (useContentLength && (header == null || header.get(0).equals("0"))) {
        if (logHeaders)
          log.info(
              "Request: method={}, uri={}, headers={}",
              exchange.getRequest().getMethod(),
              exchange.getRequest().getURI().getPath(),
              exchange.getRequest().getHeaders());
        else
          log.info(
              "Request: method={}, uri={}",
              exchange.getRequest().getMethod(),
              exchange.getRequest().getURI().getPath());
      }
      ServerWebExchangeDecorator exchangeDecorator =
          new ServerWebExchangeDecorator(exchange) {
            @Override
            public ServerHttpRequest getRequest() {
              return new RequestLoggingInterceptor(super.getRequest());
            }

            @Override
            public ServerHttpResponse getResponse() {
              return new ResponseLoggingInterceptor(super.getResponse(), startTime, logHeaders);
            }
          };
      return chain
          .filter(exchangeDecorator)
          .doOnSuccess(
              aVoid -> {
                logResponse(
                    startTime,
                    exchangeDecorator.getResponse(),
                    exchangeDecorator.getResponse().getStatusCode().value());
              })
          .doOnError(
              throwable -> {
                logResponse(startTime, exchangeDecorator.getResponse(), 500);
              });
    }
  }

  private void logResponse(long startTime, ServerHttpResponse response, int overriddenStatus) {
    final long duration = System.currentTimeMillis() - startTime;
    List<String> header = response.getHeaders().get("Content-Length");
    if (useContentLength && (header == null || header.get(0).equals("0"))) {
      if (logHeaders) {
        log.info(
            "Response({} ms): status={}, headers={}",
            "X-Response-Time: " + duration,
            "X-Response-Status: " + overriddenStatus,
            response.getHeaders());
      } else {
        log.info(
            "Response({} ms): status={}",
            "X-Response-Time: " + duration,
            "X-Response-Status: " + overriddenStatus);
      }
    }
  }
}
