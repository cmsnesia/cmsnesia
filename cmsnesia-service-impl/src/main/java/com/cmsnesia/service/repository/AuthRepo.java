package com.cmsnesia.service.repository;

import com.cmsnesia.domain.Auth;
import com.cmsnesia.service.repository.custom.AuthRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthRepo extends ReactiveMongoRepository<Auth, String>, AuthRepoCustom {

  Mono<Auth> findByUsername(String username);
}
