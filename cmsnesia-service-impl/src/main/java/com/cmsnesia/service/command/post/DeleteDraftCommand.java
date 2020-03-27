package com.cmsnesia.service.command.post;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.model.enums.PostStatus;
import com.cmsnesia.domain.repository.PostDraftRepo;
import com.cmsnesia.domain.repository.PostRepo;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.service.command.AbstractCommand;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@RequiredArgsConstructor
@Service("postDeleteDraftCommand")
public class DeleteDraftCommand extends AbstractCommand<PostDto, Result<PostDto>> {

  private final PostAssembler postAssembler;
  private final PostRepo postRepo;
  private final PostDraftRepo postDraftRepo;

  @Override
  public Publisher<Result<PostDto>> execute(Session session, PostDto dto) {
    return postDraftRepo
        .deleteById(session, dto.getId())
        .flatMap(
            postDraft ->
                postRepo
                    .findAndModifyStatus(
                        session, dto.getId(), new HashSet<>(Arrays.asList(PostStatus.PUBLISHED)))
                    .map(
                        post ->
                            Result.build(
                                postAssembler.fromDraft(postDraft), StatusCode.DELETE_SUCCESS))
                    .defaultIfEmpty(
                        Result.build(
                            postAssembler.fromDraft(postDraft), StatusCode.DELETE_FAILED)));
  }
}
