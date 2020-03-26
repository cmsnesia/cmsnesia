package com.cmsnesia.service.command.post;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.repository.PostRepo;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.command.Command;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FindById implements Command<IdRequest, Result<PostDto>> {

  private final PostAssembler postAssembler;
  private final PostRepo postRepo;

  @Override
  public Publisher<Result<PostDto>> execute(Session session, IdRequest request) {
    return postRepo
        .find(session, request.getId())
        .map(postAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND))
        .defaultIfEmpty(Result.build(StatusCode.DATA_NOT_FOUND));
  }
}
