package com.cmsnesia.service.command.post;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.PostDraft;
import com.cmsnesia.domain.repository.PostDraftRepo;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.command.AbstractCommand;
import com.cmsnesia.service.command.Command;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service("postFindDraftByIdCommand")
public class FindDraftByIdCommand extends AbstractCommand<IdRequest, Result<PostDto>> {

  private final PostAssembler postAssembler;
  private final PostDraftRepo postDraftRepo;

  @Override
  public Publisher<Result<PostDto>> execute(Session session, IdRequest request) {
    return postDraftRepo
        .find(session, request.getId())
        .defaultIfEmpty(PostDraft.builder().build())
        .map(
            postDraft -> {
              if (StringUtils.isEmpty(postDraft.getId())) {
                return Result.build(StatusCode.DATA_NOT_FOUND);
              } else {
                return Result.build(postAssembler.fromDraft(postDraft), StatusCode.DATA_FOUND);
              }
            });
  }
}
