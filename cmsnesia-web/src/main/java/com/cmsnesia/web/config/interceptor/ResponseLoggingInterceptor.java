package com.cmsnesia.web.config.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ResponseLoggingInterceptor extends ServerHttpResponseDecorator {

  private long startTime;
  private boolean logHeaders;

  public ResponseLoggingInterceptor(
      ServerHttpResponse delegate, long startTime, boolean logHeaders) {
    super(delegate);
    this.startTime = startTime;
    this.logHeaders = logHeaders;
  }

  @Override
  public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
    Flux<DataBuffer> buffer = Flux.from(body);
    return super.writeWith(
        buffer.doOnNext(
            dataBuffer -> {
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              try {
                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                // String bodyRes = IOUtils.toString(baos.toByteArray(), "UTF-8");
                if (logHeaders) {
                  log.info(
                      "Response({} ms): status={}, payload={}",
                      "X-Response-Time:" + (System.currentTimeMillis() - startTime),
                      "X-Response-Status:" + getStatusCode());
                } else {
                  log.info(
                      "Response({} ms): status={}, payload={}",
                      "X-Response-Time:" + (System.currentTimeMillis() - startTime),
                      "X-Response-Status:" + getStatusCode());
                }
              } catch (IOException e) {
                log.error(e.getMessage());
              } finally {
                try {
                  baos.close();
                } catch (IOException e) {
                  log.error(e.getMessage());
                }
              }
            }));
  }
}
