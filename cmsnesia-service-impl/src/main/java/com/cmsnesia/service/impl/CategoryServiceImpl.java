package com.cmsnesia.service.impl;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.CategoryService;

import java.util.*;

import com.cmsnesia.service.command.Command;
import com.cmsnesia.service.command.CommandExecutor;
import com.cmsnesia.service.command.category.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

  private final CommandExecutor commandExecutor;

  @Override
  public Mono<Result<CategoryDto>> add(Session session, CategoryDto dto) {
    return Mono.from(commandExecutor.execute(CreateCommand.class, session, dto));
  }

  @Override
  public Mono<Result<CategoryDto>> edit(Session session, CategoryDto dto) {
    return Mono.from(commandExecutor.execute(EditCommand.class, session, dto));
  }

  @Override
  public Mono<Result<CategoryDto>> delete(Session session, CategoryDto dto) {
    return Mono.from(commandExecutor.execute(DeleteCommand.class, session, dto));
  }

  @Override
  public Mono<Page<CategoryDto>> find(Session session, CategoryDto dto, Pageable pageable) {
    Command.PageableRequest<CategoryDto> request =
        new Command.PageableRequest<CategoryDto>(dto, pageable);
    return Mono.from(commandExecutor.execute(FindAllCommand.class, session, request));
  }

  @Override
  public Mono<Result<CategoryDto>> find(Session session, IdRequest idRequest) {
    return Mono.from(
        commandExecutor.execute(
            FindByIdOrLinkCommand.class, session, CategoryDto.builder().id(idRequest.getId()).build()));
  }

  @Override
  public Mono<Set<CategoryDto>> findByIds(Session session, Set<IdRequest> ids) {
    return Mono.from(commandExecutor.execute(FindByIdsCommand.class, session, ids));
  }

  @Override
  public Mono<Result<Boolean>> exists(Session session, Set<IdRequest> ids) {
    return Mono.from(commandExecutor.execute(MustExistCommand.class, session, ids));
  }
}
