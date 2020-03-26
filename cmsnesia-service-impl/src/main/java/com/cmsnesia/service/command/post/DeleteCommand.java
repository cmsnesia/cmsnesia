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
import com.cmsnesia.service.command.Command;
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
public class DeleteCommand implements Command<PostDto, Result<PostDto>> {

  private final PostRepo postRepo;
  private final PostDraftRepo postDraftRepo;

  @Override
  public Publisher<Result<PostDto>> execute(Session session, PostDto dto) {
    Mono<Boolean> postIsExist = postIsExist(session, dto);
    return postIsExist.flatMap(
        exist -> {
          if (exist) {
            return postRepo
                .find(session, dto.getId())
                .flatMap(
                    post -> {
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
                    });
          } else {
            return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
          }
        });
  }

  private Mono<Boolean> postIsExist(Session session, PostDto postDto) {
    return postRepo.exists(session, new HashSet<>(Arrays.asList(postDto.getId())));
  }
}
