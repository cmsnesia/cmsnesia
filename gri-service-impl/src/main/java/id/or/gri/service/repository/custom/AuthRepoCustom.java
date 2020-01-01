package id.or.gri.service.repository.custom;

import id.or.gri.domain.Auth;
import id.or.gri.model.AuthDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthRepoCustom {

    Flux<Auth> find(AuthDto authDto, AuthDto dto, Pageable pageable);

    Mono<Long> countFind(AuthDto authDto, AuthDto dto);

}
