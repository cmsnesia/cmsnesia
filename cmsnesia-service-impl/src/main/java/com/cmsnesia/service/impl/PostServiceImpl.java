package com.cmsnesia.service.impl;

import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.PostDraft;
import com.cmsnesia.domain.model.Author;
import com.cmsnesia.domain.model.Tag;
import com.cmsnesia.domain.model.enums.PostStatus;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.CategoryService;
import com.cmsnesia.service.PostService;
import com.cmsnesia.service.repository.PostDraftRepo;
import com.cmsnesia.service.repository.PostRepo;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PostServiceImpl implements PostService {

  private final PostAssembler postAssembler;
  private final PostRepo postRepo;
  private final PostDraftRepo postDraftRepo;
  private final CategoryService categoryService;

  public PostServiceImpl(
      PostAssembler postAssembler,
      PostRepo postRepo,
      PostDraftRepo postDraftRepo,
      CategoryService categoryService) {
    this.postAssembler = postAssembler;
    this.postRepo = postRepo;
    this.postDraftRepo = postDraftRepo;
    this.categoryService = categoryService;
  }

  @Override
  public Mono<Result<PostDto>> add(AuthDto authDto, PostDto dto) {
    PostDraft post = postAssembler.fromPostDto(dto);

    post.setId(UUID.randomUUID().toString());

    post.setCreatedBy(authDto.getId());
    post.setCreatedAt(new Date());
    post.setStatus(Arrays.asList(PostStatus.UNPUBLISHED).stream().collect(Collectors.toSet()));

    post.getTags().forEach(tag -> tag.setCreatedBy(authDto.getId()));

    return categoryService
        .exists(
            authDto,
            dto.getCategories().stream().map(CategoryDto::getId).collect(Collectors.toSet()))
        .flatMap(
            categotyIsExist -> {
                if (categotyIsExist != null && categotyIsExist.getData() != null && categotyIsExist.getData()) {
                    post.getTags().forEach(tag -> tag.setCreatedBy(authDto.getId()));
                    return postDraftRepo.save(post).map(postAssembler::fromDraft)
                            .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
                } else {
                    return Mono.just(Result.build(StatusCode.SAVE_FAILED));
                }
            });
  }

  @Override
  public Mono<Result<PostDto>> edit(AuthDto authDto, PostDto dto) {
    return postDraftRepo
        .findById(dto.getId())
        .flatMap(
            (Function<PostDraft, Mono<Result<PostDto>>>)
                post -> {
                  PostDraft save = postAssembler.fromPostDto(dto);

                  post.setStatus(
                      Arrays.asList(PostStatus.UNPUBLISHED).stream().collect(Collectors.toSet()));

                  save.audit(post);

                  save.setModifiedBy(authDto.getId());
                  save.setModifiedAt(new Date());

                  Set<Tag> tags = new HashSet<>();
                  save.getTags()
                      .forEach(
                          tag -> {
                            post.getTags()
                                .forEach(
                                    existing -> {
                                      if (!(tag.getName().equals(existing.getName())
                                          && tag.getCreatedBy().equals(existing.getCreatedBy()))) {
                                        tag.setCreatedBy(authDto.getId());
                                        tags.add(tag);
                                      }
                                    });
                          });
                  save.setTags(tags);

                  return categoryService
                      .exists(
                          authDto,
                          dto.getCategories().stream()
                              .map(CategoryDto::getId)
                              .collect(Collectors.toSet()))
                      .flatMap(
                          categotyIsExist -> {
                              if (categotyIsExist != null && categotyIsExist.getData() != null && categotyIsExist.getData()) {
                              return postDraftRepo
                                  .save(save)
                                  .map(saved -> postAssembler.fromDraft(saved))
                                  .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
                            } else {
                              return Mono.just(Result.build(StatusCode.SAVE_FAILED));
                            }
                          });
                });
  }

  @Override
  public Mono<Result<PostDto>> delete(AuthDto authDto, PostDto dto) {
    return postDraftRepo
        .findById(dto.getId())
        .flatMap(
            (Function<PostDraft, Mono<Result<PostDto>>>)
                post -> {
                  post.setDeletedBy(authDto.getId());
                  post.setDeletedAt(new Date());
                  post.setStatus(
                      Arrays.asList(PostStatus.UNPUBLISHED).stream().collect(Collectors.toSet()));
                  return postDraftRepo.save(post).map(saved -> postAssembler.fromDraft(saved))
                          .map(result -> Result.build(result, StatusCode.DELETE_SUCCESS));
                });
  }

  @Override
  public Mono<Page<PostDto>> find(AuthDto authDto, PostDto dto, Pageable pageable) {
    return postRepo
        .countFind(authDto, dto)
        .flatMap(
            count -> {
              Mono<List<PostDto>> mono =
                  postRepo
                      .find(authDto, dto, pageable)
                      .map(post -> postAssembler.fromEntity(post))
                      .collectList();
              return mono.map(postDtos -> new PageImpl<>(postDtos, pageable, count));
            });
  }

  @Override
  public Mono<Page<PostDto>> findDraft(AuthDto authDto, PostDto dto, Pageable pageable) {
    return postDraftRepo
        .countFind(authDto, dto)
        .flatMap(
            count -> {
              Mono<List<PostDto>> mono =
                  postDraftRepo
                      .find(authDto, dto, pageable)
                      .map(postDraft -> postAssembler.fromDraft(postDraft))
                      .collectList();
              return mono.map(postDtos -> new PageImpl<>(postDtos, pageable, count));
            });
  }

  @Override
  public Mono<Result<PostDto>> publish(AuthDto session, IdRequest id) {
    return postDraftRepo
        .findById(id.getId())
        .flatMap(
            postDraft -> {
              Post newPost = postAssembler.fromDto(postAssembler.fromDraft(postDraft));
              newPost.setCreatedAt(new Date());
              newPost.setCreatedBy(session.getId());
              return postRepo
                  .findById(id.getId())
                  .defaultIfEmpty(newPost)
                  .flatMap(
                      post -> {
                        Set<Author> authors =
                            post.getAuthors() == null ? new HashSet<>() : post.getAuthors();
                        if (authors.stream()
                            .map(Author::getName)
                            .noneMatch(name -> name.equals(session.getFullName()))) {
                          Author author = new Author();
                          author.setName(session.getFullName());
                          author.setModifiedAt(new Date());
                          authors.add(author);
                        }
                        Post exitingPost =
                            postAssembler.fromDto(postAssembler.fromDraft(postDraft));
                        exitingPost.setAuthors(authors);
                        exitingPost.audit(post);
                        exitingPost.setModifiedAt(new Date());
                        exitingPost.setModifiedBy(session.getId());

                        // blocking part
                        exitingPost
                            .getCategories()
                            .forEach(
                                category -> {
                                  Result<CategoryDto> result =
                                      categoryService.findById(session, category.getId()).block();
                                  if (result.getStatusCode().equals(StatusCode.DATA_FOUND)) {
                                      category.setName(result.getData().getName());
                                  }
                                });

                        return postRepo
                            .save(exitingPost)
                            .flatMap(
                                saved -> {
                                  postDraft.setStatus(
                                      Arrays.asList(PostStatus.PUBLISHED).stream()
                                          .collect(Collectors.toSet()));
                                  return postDraftRepo
                                      .save(postDraft)
                                      .map(result -> postAssembler.fromEntity(saved))
                                      .map(returned -> Result.build(returned, StatusCode.SAVE_SUCCESS));
                                });
                      });
            });
  }
}
