package com.cmsnesia.service;

import com.cmsnesia.model.AuthDto;
import reactor.core.publisher.Mono;

public interface AuthService extends BaseService<AuthDto> {

  Mono<AuthDto> findByUsername(String username);
}
