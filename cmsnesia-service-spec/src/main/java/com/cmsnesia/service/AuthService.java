package com.cmsnesia.service;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.api.Result;
import reactor.core.publisher.Mono;

public interface AuthService extends BaseService<AuthDto> {

  Mono<Result<AuthDto>> findByUsername(String username);

  Mono<Result<AuthDto>> changePassword(AuthDto session, String newPassword);
}
