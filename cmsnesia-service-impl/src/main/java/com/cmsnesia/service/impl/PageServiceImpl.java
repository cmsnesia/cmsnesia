package com.cmsnesia.service.impl;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.model.PageDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.PageService;

import com.cmsnesia.service.command.Command;
import com.cmsnesia.service.command.CommandExecutor;
import com.cmsnesia.service.command.page.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class PageServiceImpl implements PageService {

  private final CommandExecutor commandExecutor;

  @Override
  public Mono<Result<PageDto>> add(Session session, PageDto dto) {
    return Mono.from(commandExecutor.execute(CreateCommand.class, session, dto));
  }

  @Override
  public Mono<Result<PageDto>> edit(Session session, PageDto dto) {
    return Mono.from(commandExecutor.execute(EditCommand.class, session, dto));
  }

  @Override
  public Mono<Result<PageDto>> delete(Session session, PageDto dto) {
    return Mono.from(commandExecutor.execute(DeleteCommand.class, session, dto));
  }

  @Override
  public Mono<org.springframework.data.domain.Page<PageDto>> find(
      Session session, PageDto dto, Pageable pageable) {
    Command.PageableRequest<PageDto> request = new Command.PageableRequest(dto, pageable);
    return Mono.from(commandExecutor.execute(FindAllCommand.class, session, request));
  }

  @Override
  public Mono<Result<PageDto>> find(Session session, IdRequest idRequest) {
    return Mono.from(
        commandExecutor.execute(
            FindByIdOrLinkCommand.class, session, PageDto.builder().id(idRequest.getId()).build()));
  }
}
