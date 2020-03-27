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
import com.cmsnesia.service.command.AbstractCommand;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("postEditDraftCommand")
public class EditDraftCommand extends AbstractCommand<PostDto, Result<PostDto>> {

  private final PostAssembler postAssembler;
  private final PostRepo postRepo;
  private final PostDraftRepo postDraftRepo;
  private final CategoryRepo categoryRepo;

  @Transactional
  @Override
  public Publisher<Result<PostDto>> execute(Session session, PostDto dto) {
    return postRepo
        .exists(session, null, dto.getTitle(), dto.getLink())
        .flatMap(
            exists -> {
              if (!exists) {
                return postDraftRepo
                    .find(session, dto.getId())
                    .defaultIfEmpty(PostDraft.builder().build())
                    .flatMap(
                        postDraft -> {
                          if (StringUtils.isEmpty(postDraft.getId())) {
                            return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
                          } else {
                            return findCategories(session, dto)
                                .flatMap(
                                    categories -> {
                                      if (categories.size() != dto.getCategories().size()) {
                                        return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
                                      } else {
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
                                                validate(save)
                                                    .flatMap(
                                                        o ->
                                                            postDraftRepo
                                                                .save(save)
                                                                .map(postAssembler::fromDraft)
                                                                .map(
                                                                    postDto ->
                                                                        Result.build(
                                                                            postDto,
                                                                            StatusCode
                                                                                .SAVE_SUCCESS))));
                                      }
                                    });
                          }
                        });
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
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
