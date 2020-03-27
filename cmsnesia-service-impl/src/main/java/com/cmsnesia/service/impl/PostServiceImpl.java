package com.cmsnesia.service.impl;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.command.Command;
import com.cmsnesia.service.command.CommandExecutor;
import com.cmsnesia.service.PostService;
import com.cmsnesia.service.command.post.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class PostServiceImpl implements PostService {

  private final CommandExecutor commandExecutor;

  @Override
  public Mono<Result<PostDto>> add(Session session, PostDto dto) {
    return Mono.from(commandExecutor.execute(CreateCommand.class, session, dto));
  }

  @Transactional
  @Override
  public Mono<Result<PostDto>> edit(Session session, PostDto dto) {
    Mono<Result<PostDto>> draftSaved =
        Mono.from(commandExecutor.execute(EditCommand.class, session, dto));
    Mono<Result<PostDto>> postSaved =
        Mono.from(commandExecutor.execute(EditDraftCommand.class, session, dto));
    return Mono.zip(draftSaved, postSaved)
        .map(
            tuple -> {
              if (tuple.getT1().getStatusCode() == StatusCode.SAVE_SUCCESS
                  && tuple.getT2().getStatusCode() == StatusCode.SAVE_SUCCESS) {
                return tuple.getT2();
              } else {
                return Result.build(StatusCode.SAVE_FAILED);
              }
            });
  }

  @Transactional
  @Override
  public Mono<Result<PostDto>> editDraft(Session session, PostDto dto) {
    return Mono.from(commandExecutor.execute(EditDraftCommand.class, session, dto));
  }

  @Transactional
  @Override
  public Mono<Result<PostDto>> delete(Session session, PostDto dto) {
    return Mono.from(commandExecutor.execute(DeleteCommand.class, session, dto));
  }

  @Override
  public Mono<Page<PostDto>> find(Session session, PostDto dto, Pageable pageable) {
    Command.PageableRequest<PostDto> request = new Command.PageableRequest<PostDto>(dto, pageable);
    return Mono.from(commandExecutor.execute(FindAllCommand.class, session, request));
  }

  @Override
  public Mono<Page<PostDto>> findDraft(Session session, PostDto dto, Pageable pageable) {
    Command.PageableRequest<PostDto> request = new Command.PageableRequest<PostDto>(dto, pageable);
    return Mono.from(commandExecutor.execute(FindAllDraftCommand.class, session, request));
  }

  @Override
  public Mono<Result<PostDto>> find(Session session, IdRequest idRequest) {
    return Mono.from(
        commandExecutor.execute(
            FindByIdOrLinkCommand.class, session, PostDto.builder().id(idRequest.getId()).build()));
  }

  @Override
  public Mono<Result<PostDto>> findDraft(Session session, IdRequest idRequest) {
    return Mono.from(commandExecutor.execute(FindDraftByIdCommand.class, session, idRequest));
  }

  @Transactional
  @Override
  public Mono<Result<PostDto>> publish(Session session, IdRequest id) {
    return Mono.from(commandExecutor.execute(PublishCommand.class, session, id));
  }

  @Override
  public Mono<Result<PostDto>> deleteDraft(Session session, PostDto dto) {
    return Mono.from(commandExecutor.execute(DeleteDraftCommand.class, session, dto));
  }

  @Override
  public Mono<Result<PostDto>> findByIdOrLink(Session session, PostDto dto) {
    return Mono.from(commandExecutor.execute(FindByIdOrLinkCommand.class, session, dto));
  }
}
