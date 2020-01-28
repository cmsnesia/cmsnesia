package com.cmsnesia.web.config.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

@Slf4j
public class RequestLoggingInterceptor extends ServerHttpRequestDecorator {

  public RequestLoggingInterceptor(ServerHttpRequest delegate) {
    super(delegate);
  }

  @Override
  public Flux<DataBuffer> getBody() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    return super.getBody()
        .doOnNext(
            dataBuffer -> {
              try {
                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                String body = IOUtils.toString(baos.toByteArray(), "UTF-8");
                log.info(
                    "Request: method={}, uri={}, payload={}",
                    getDelegate().getMethod(),
                    getDelegate().getPath(),
                    body);
              } catch (IOException e) {
                log.error(e.getMessage());
              } finally {
                try {
                  baos.close();
                } catch (IOException e) {
                  log.error(e.getMessage());
                }
              }
            });
  }
}
