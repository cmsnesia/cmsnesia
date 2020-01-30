package com.cmsnesia.service.impl;

import com.cmsnesia.assembler.AuthAssembler;
import com.cmsnesia.domain.Auth;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.service.AuthService;
import com.cmsnesia.service.repository.AuthRepo;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceImpl implements AuthService {

  private AuthAssembler authAssembler;
  private AuthRepo authRepo;

  public AuthServiceImpl(AuthAssembler authAssembler, AuthRepo authRepo) {
    this.authAssembler = authAssembler;
    this.authRepo = authRepo;
  }

  @Override
  public Mono<Result<AuthDto>> add(AuthDto authDto, AuthDto dto) {
    Auth auth = authAssembler.fromDto(dto);
    auth.setId(UUID.randomUUID().toString());
    auth.setCreatedBy(authDto.getId());
    auth.setCreatedAt(new Date());
    return authRepo.save(auth).map(authAssembler::fromEntity)
            .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
  }

  @Override
  public Mono<Result<AuthDto>> edit(AuthDto authDto, AuthDto dto) {
    return authRepo
        .findById(dto.getId())
        .flatMap((Function<Auth, Mono<Result<AuthDto>>>) auth -> {
            Auth save = authAssembler.fromDto(dto);
            save.audit(auth);
            save.setModifiedBy(authDto.getId());
            save.setModifiedAt(new Date());
            return authRepo.save(save).map(saved -> authAssembler.fromEntity(saved))
                    .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
        });
  }

  @Override
  public Mono<Result<AuthDto>> delete(AuthDto authDto, AuthDto dto) {
    return authRepo
        .findById(dto.getId())
        .flatMap(
            (Function<Auth, Mono<Result<AuthDto>>>)
                auth -> {
                  auth.setDeletedBy(authDto.getId());
                  auth.setDeletedAt(new Date());
                  return authRepo.save(auth).map(saved -> authAssembler.fromEntity(saved))
                          .map(result -> Result.build(result, StatusCode.DELETE_SUCCESS));
                });
  }

  @Override
  public Mono<Page<AuthDto>> find(AuthDto authDto, AuthDto dto, Pageable pageable) {
    return authRepo
        .countFind(authDto, dto)
        .flatMap(
            count -> {
              Mono<List<AuthDto>> mono =
                  authRepo
                      .find(authDto, dto, pageable)
                      .map(auth -> authAssembler.fromEntity(auth))
                      .collectList();
              return mono.map(authDtos -> new PageImpl<>(authDtos, pageable, count));
            });
  }

  @Override
  public Mono<Result<AuthDto>> findByUsername(String username) {
    return authRepo
        .findByUsername(username)
        .map(
            auth -> {
              AuthDto authDto = authAssembler.fromEntity(auth);
              authDto.setPassword(auth.getPassword());
              return Result.build(authDto, StatusCode.DATA_FOUND);
            });
  }
}
