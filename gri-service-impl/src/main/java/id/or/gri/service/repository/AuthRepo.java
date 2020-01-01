package id.or.gri.service.repository;

import id.or.gri.domain.Auth;
import id.or.gri.service.repository.custom.AuthRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthRepo extends ReactiveMongoRepository<Auth, String>, AuthRepoCustom {

    Mono<Auth> findByUsername(String username);

}
