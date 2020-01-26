package com.cmsnesia.service.impl;

import com.cmsnesia.assembler.AuthAssembler;
import cmsnesia.domain.Auth;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.service.repository.AuthRepo;
import com.cmsnesia.service.AuthService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class AuthServiceImpl implements AuthService {

    private AuthAssembler authAssembler;
    private AuthRepo authRepo;

    public AuthServiceImpl(AuthAssembler authAssembler, AuthRepo authRepo) {
        this.authAssembler = authAssembler;
        this.authRepo = authRepo;
    }

    @Override
    public Mono<AuthDto> add(AuthDto authDto, AuthDto dto) {
        Auth auth = authAssembler.fromDto(dto);
        auth.setId(UUID.randomUUID().toString());
        auth.setCreatedBy(authDto.getId());
        auth.setCreatedAt(new Date());
        return authRepo.save(auth).map(authAssembler::fromEntity);
    }

    @Override
    public Mono<AuthDto> edit(AuthDto authDto, AuthDto dto) {
        return authRepo.findById(dto.getId())
                .flatMap((Function<Auth, Mono<AuthDto>>) auth -> {
                    Auth save = authAssembler.fromDto(dto);
                    save.audit(auth);
                    save.setModifiedBy(authDto.getId());
                    save.setModifiedAt(new Date());
                    return authRepo
                            .save(save)
                            .map(result -> authAssembler.fromEntity(result));
                });
    }

    @Override
    public Mono<AuthDto> delete(AuthDto authDto, AuthDto dto) {
        return authRepo.findById(dto.getId())
                .flatMap((Function<Auth, Mono<AuthDto>>) auth -> {
                    auth.setDeletedBy(authDto.getId());
                    auth.setDeletedAt(new Date());
                    return authRepo
                            .save(auth)
                            .map(result -> authAssembler.fromEntity(result));
                });
    }

    @Override
    public Flux<AuthDto> find(AuthDto authDto, AuthDto dto, Pageable pageable) {
        return authRepo.find(authDto, dto, pageable)
                .map(auth -> authAssembler.fromEntity(auth));
    }

    @Override
    public Mono<AuthDto> findByUsername(String username) {
        return authRepo.findByUsername(username)
                .map(auth -> {
                    AuthDto authDto = authAssembler.fromEntity(auth);
                    authDto.setPassword(auth.getPassword());
                    return authDto;
                });
    }
}