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
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("postEditCommand")
public class EditCommand extends AbstractCommand<PostDto, Result<PostDto>> {

  private final PostAssembler postAssembler;
  private final PostRepo postRepo;
  private final PostDraftRepo postDraftRepo;
  private final CategoryRepo categoryRepo;

  @Override
  public Publisher<Result<PostDto>> execute(Session session, PostDto dto) {
    return postRepo
        .find(session, dto.getId(), dto.getLink())
        .defaultIfEmpty(Post.builder().build())
        .flatMap(
            post -> {
              if (StringUtils.isEmpty(post.getId())) {
                return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
              } else {
                Mono<Set<Category>> categoryList = findCategories(session, post);
                return categoryList.flatMap(
                    categories -> {
                      if (categories.size() != post.getCategories().size()) {
                        return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
                      } else {
                        PostDraft postDraft = postAssembler.fromPost(post);
                        postDraft.setCategories(categories);
                        postDraft.setApplications(Session.applications(session));

                        postDraft.audit(post);
                        postDraft.setStatus(
                            Arrays.asList(PostStatus.UNPUBLISHED.name()).stream()
                                .collect(Collectors.toSet()));

                        return validate(postDraft)
                            .flatMap(o -> postDraftRepo.save(postDraft))
                            .map(
                                saved ->
                                    Result.build(
                                        postAssembler.fromDraft(saved), StatusCode.SAVE_SUCCESS));
                      }
                    });
              }
            });
  }

  private Mono<Set<Category>> findCategories(Session session, Post post) {
    return categoryRepo
        .find(
            session, post.getCategories().stream().map(Category::getId).collect(Collectors.toSet()))
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
