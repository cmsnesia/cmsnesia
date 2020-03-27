package com.cmsnesia.service.command.post;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.PostDraft;
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
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

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
        .defaultIfEmpty(PostDraft.builder().build())
        .flatMap(
            postDraft -> {
              if (StringUtils.isEmpty(postDraft.getId())) {
                return Mono.just(Result.build(StatusCode.DELETE_FAILED));
              } else {
                return postRepo
                    .findAndModifyStatus(
                        session, dto.getId(), new HashSet<>(Arrays.asList(PostStatus.PUBLISHED)))
                    .defaultIfEmpty(Post.builder().build())
                    .map(
                        post -> {
                          if (StringUtils.isEmpty(post.getId())) {
                            return Result.build(StatusCode.DELETE_FAILED);
                          } else {
                            return Result.build(
                                postAssembler.fromEntity(post), StatusCode.DELETE_SUCCESS);
                          }
                        });
              }
            });
  }
}
