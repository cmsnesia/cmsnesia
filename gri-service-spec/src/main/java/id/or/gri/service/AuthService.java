package id.or.gri.service;

import id.or.gri.model.AuthDto;
import reactor.core.publisher.Mono;

public interface AuthService extends BaseService<AuthDto> {

    Mono<AuthDto> findByUsername(String username);

}
