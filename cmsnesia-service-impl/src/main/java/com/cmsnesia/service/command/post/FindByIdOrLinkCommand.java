package com.cmsnesia.service.command.post;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.repository.PostRepo;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.service.command.AbstractCommand;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service("postFindByIdOrLinkCommand")
public class FindByIdOrLinkCommand extends AbstractCommand<PostDto, Result<PostDto>> {

  private final PostAssembler postAssembler;
  private final PostRepo postRepo;

  @Override
  public Publisher<Result<PostDto>> execute(Session session, PostDto request) {
    return postRepo
        .find(session, request.getId(), request.getLink())
        .defaultIfEmpty(Post.builder().build())
        .map(
            post -> {
              if (StringUtils.isEmpty(post.getId())) {
                return Result.build(StatusCode.DATA_NOT_FOUND);
              } else {
                return Result.build(postAssembler.fromEntity(post), StatusCode.DATA_FOUND);
              }
            });
  }
}
