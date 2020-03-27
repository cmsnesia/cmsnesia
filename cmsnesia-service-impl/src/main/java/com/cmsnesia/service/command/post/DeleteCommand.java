package com.cmsnesia.service.command.post;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.model.enums.PostStatus;
import com.cmsnesia.domain.repository.CategoryRepo;
import com.cmsnesia.domain.repository.PostDraftRepo;
import com.cmsnesia.domain.repository.PostRepo;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.service.command.AbstractCommand;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DeleteCommand extends AbstractCommand<PostDto, Result<PostDto>> {

  private final PostRepo postRepo;
  private final PostDraftRepo postDraftRepo;

  @Override
  public Publisher<Result<PostDto>> execute(Session session, PostDto dto) {
    return postRepo
        .find(session, dto.getId(), dto.getLink())
        .defaultIfEmpty(null)
        .flatMap(
            post -> {
              if (post == null) {
                return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
              } else {
                post.setDeletedBy(session.getId());
                post.setDeletedAt(new Date());
                post.setStatus(
                    Arrays.asList(PostStatus.UNPUBLISHED.name()).stream()
                        .collect(Collectors.toSet()));
                return postRepo
                    .save(post)
                    .flatMap(
                        saved ->
                            postDraftRepo
                                .deleteById(post.getId())
                                .map(result -> Result.build(dto, StatusCode.DELETE_SUCCESS)));
              }
            });
  }
}
