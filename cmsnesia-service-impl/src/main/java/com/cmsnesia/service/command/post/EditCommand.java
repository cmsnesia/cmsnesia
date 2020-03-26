package com.cmsnesia.service.command.post;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.PostDraft;
import com.cmsnesia.domain.model.Category;
import com.cmsnesia.domain.repository.CategoryRepo;
import com.cmsnesia.domain.repository.PostDraftRepo;
import com.cmsnesia.domain.repository.PostRepo;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.service.command.Command;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EditCommand implements Command<PostDto, Result<PostDto>> {

  private final PostAssembler postAssembler;
  private final PostRepo postRepo;
  private final PostDraftRepo postDraftRepo;
  private final CategoryRepo categoryRepo;

  @Override
  public Publisher<Result<PostDto>> execute(Session session, PostDto dto) {
    Mono<Boolean> postIsExist = postIsExist(session, dto);
    Mono<Set<Category>> categoryList = findCategories(session, dto);
    return Mono.zip(postIsExist, categoryList)
        .flatMap(
            tuple -> {
              if (tuple.getT1()) {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
              if (tuple.getT2().size() != dto.getCategories().size()) {
                return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
              }

              return postRepo
                  .find(session, dto.getId())
                  .flatMap(
                      post -> {
                        PostDraft postDraft = postAssembler.fromPost(post);
                        postDraft.setApplications(Session.applications(session));
                        return postDraftRepo.save(postDraft);
                      })
                  .map(
                      postDraft -> {
                        PostDto postDto = postAssembler.fromDraft(postDraft);
                        return Result.build(postDto, StatusCode.SAVE_SUCCESS);
                      });
            });
  }

  private Mono<Boolean> postIsExist(Session session, PostDto postDto) {
    return postRepo.exists(session, new HashSet<>(Arrays.asList(postDto.getId())));
  }

  private Mono<Set<Category>> findCategories(Session session, PostDto postDto) {
    return categoryRepo
        .find(
            session,
            postDto.getCategories().stream().map(CategoryDto::getId).collect(Collectors.toSet()))
        .collectList()
        .map(
            categories ->
                categories.stream()
                    .map(
                        category ->
                            Category.builder()
                                .id(category.getId())
                                .name(category.getName())
                                .build())
                    .collect(Collectors.toSet()));
  }
}
