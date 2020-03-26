package com.cmsnesia.service.command.post;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CreatePostCommand implements Command<PostDto, PostDto> {

  private final PostAssembler postAssembler;
  private final PostRepo postRepo;
  private final PostDraftRepo postDraftRepo;
  private final CategoryRepo categoryRepo;

  @Override
  public Mono<Result<PostDto>> execute(Session session, PostDto dto) {
    Mono<Boolean> postTitleIsExist = postTitleIsExist(session, dto);
    Mono<Boolean> postLinkIsExist = postLinkIsExist(session, dto);
    Mono<Set<Category>> categoryList = findCategories(session, dto);
    return Mono.zip(postTitleIsExist, postLinkIsExist, categoryList)
        .flatMap(
            tuple -> {
              if (tuple.getT1()) {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
              if (tuple.getT2()) {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
              if (tuple.getT3().size() != dto.getCategories().size()) {
                return Mono.just(Result.build(StatusCode.DATA_FOUND));
              }

              Set<Category> categories = tuple.getT3();

              PostDraft postDraft = postAssembler.fromPostDto(dto);
              postDraft.setId(UUID.randomUUID().toString());
              postDraft.setCreatedBy(session.getId());
              postDraft.setCreatedAt(new Date());

              postDraft.getTags().forEach(tag -> tag.setCreatedBy(session.getId()));
              postDraft.setCategories(categories);

              postDraft.setStatus(
                  Arrays.asList(PostStatus.UNPUBLISHED.name()).stream()
                      .collect(Collectors.toSet()));

              postDraft.setApplications(Session.applications(session));

              return postDraftRepo
                  .save(postDraft)
                  .map(postAssembler::fromDraft)
                  .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
            });
  }

  private Mono<Boolean> postTitleIsExist(Session session, PostDto postDto) {
    return postRepo.exists(session, null, postDto.getTitle(), null);
  }

  private Mono<Boolean> postLinkIsExist(Session session, PostDto postDto) {
    return postRepo.exists(session, null, null, postDto.getLink());
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
