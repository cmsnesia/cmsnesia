package com.cmsnesia.service.command.post;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.PostDraft;
import com.cmsnesia.domain.model.Category;
import com.cmsnesia.domain.model.enums.PostStatus;
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
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EditDraftCommand implements Command<PostDto, Result<PostDto>> {

  private final PostAssembler postAssembler;
  private final PostRepo postRepo;
  private final PostDraftRepo postDraftRepo;
  private final CategoryRepo categoryRepo;

  @Transactional
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

              return postDraftRepo
                  .find(session, dto.getId())
                  .flatMap(
                      postDraft -> {
                        Set<Category> categories = tuple.getT2();

                        PostDraft save = postAssembler.fromPostDto(dto);

                        postDraft.setCategories(categories);
                        postDraft.setStatus(
                            Arrays.asList(PostStatus.UNPUBLISHED.name()).stream()
                                .collect(Collectors.toSet()));

                        save.audit(postDraft);

                        save.setModifiedBy(session.getId());
                        save.setModifiedAt(new Date());

                        Mono<Post> updated = updatePostStatus(session, dto);

                        return updated.flatMap(
                            post ->
                                postDraftRepo
                                    .save(save)
                                    .map(postAssembler::fromDraft)
                                    .map(
                                        postDto -> Result.build(postDto, StatusCode.SAVE_SUCCESS)));
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

  private Mono<Post> updatePostStatus(Session session, PostDto postDto) {
    return postRepo.findAndModifyStatus(
        session,
        postDto.getId(),
        new HashSet<>(Arrays.asList(PostStatus.PUBLISHED, PostStatus.DRAFTED)));
  }
}
