package com.cmsnesia.service.impl;

import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.PostDraft;
import com.cmsnesia.domain.model.Author;
import com.cmsnesia.domain.model.enums.PostStatus;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.CategoryService;
import com.cmsnesia.service.PostService;
import com.cmsnesia.service.repository.PostDraftRepo;
import com.cmsnesia.service.repository.PostRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PostServiceImpl implements PostService {

  private final PostAssembler postAssembler;
  private final PostRepo postRepo;
  private final PostDraftRepo postDraftRepo;
  private final CategoryService categoryService;

  @Override
  public Mono<Result<PostDto>> add(AuthDto authDto, PostDto dto) {
    PostDraft postDraft = postAssembler.fromPostDto(dto);

    postDraft.setId(UUID.randomUUID().toString());

    postDraft.setCreatedBy(authDto.getId());
    postDraft.setCreatedAt(new Date());
    postDraft.setStatus(Arrays.asList(PostStatus.UNPUBLISHED).stream().collect(Collectors.toSet()));

    postDraft.getTags().forEach(tag -> tag.setCreatedBy(authDto.getId()));

    Set<IdRequest> categoryIds =
        dto.getCategories().stream()
            .map(categoryDto -> IdRequest.builder().id(categoryDto.getId()).build())
            .collect(Collectors.toSet());
    return categoryService
        .exists(authDto, categoryIds)
        .flatMap(
            categotyIsExist -> {
              if (categotyIsExist != null
                  && categotyIsExist.getData() != null
                  && categotyIsExist.getData()) {
                return categoryService
                    .findByIds(authDto, categoryIds)
                    .flatMap(
                        categoryDtos -> {
                          postDraft
                              .getCategories()
                              .forEach(
                                  category -> {
                                    categoryDtos.forEach(
                                        categoryDto -> {
                                          if (categoryDto.getId().equals(category.getId())) {
                                            category.setName(categoryDto.getName());
                                          }
                                        });
                                  });
                          postDraft.getTags().forEach(tag -> tag.setCreatedBy(authDto.getId()));
                          return postDraftRepo
                              .save(postDraft)
                              .map(postAssembler::fromDraft)
                              .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
                        });
              } else {
                return Mono.just(Result.build(StatusCode.SAVE_FAILED));
              }
            });
  }

  @Transactional
  @Override
  public Mono<Result<PostDto>> edit(AuthDto session, PostDto dto) {
    return postRepo
        .findById(dto.getId())
        .flatMap(
            post -> {
              PostDraft postDraft = postAssembler.fromPost(post);
              return postDraftRepo.save(postDraft);
            })
        .flatMap(
            postDraft -> {
              PostDto postDto = postAssembler.fromDraft(postDraft);
              return editDraft(session, postDto);
            });
  }

  @Transactional
  @Override
  public Mono<Result<PostDto>> editDraft(AuthDto session, PostDto dto) {
    return postDraftRepo
        .findById(dto.getId())
        .flatMap(
            (Function<PostDraft, Mono<Result<PostDto>>>)
                postDraft -> {
                  PostDraft save = postAssembler.fromPostDto(dto);

                  postDraft.setStatus(
                      Arrays.asList(PostStatus.UNPUBLISHED).stream().collect(Collectors.toSet()));

                  save.audit(postDraft);

                  save.setModifiedBy(session.getId());
                  save.setModifiedAt(new Date());

                  Set<IdRequest> categoryIds =
                      dto.getCategories().stream()
                          .map(categoryDto -> IdRequest.builder().id(categoryDto.getId()).build())
                          .collect(Collectors.toSet());
                  return categoryService
                      .exists(session, categoryIds)
                      .flatMap(
                          categotyIsExist -> {
                            if (categotyIsExist != null
                                && categotyIsExist.getData() != null
                                && categotyIsExist.getData()) {
                              return categoryService
                                  .findByIds(session, categoryIds)
                                  .flatMap(
                                      categoryDtos -> {
                                        save.getCategories()
                                            .forEach(
                                                category -> {
                                                  categoryDtos.forEach(
                                                      categoryDto -> {
                                                        if (categoryDto
                                                            .getId()
                                                            .equals(category.getId())) {
                                                          category.setName(categoryDto.getName());
                                                        }
                                                      });
                                                });
                                        return postRepo
                                            .findAndModifyStatus(
                                                session,
                                                IdRequest.builder().id(save.getId()).build(),
                                                new HashSet<>(Arrays.asList(PostStatus.PUBLISHED, PostStatus.DRAFTED)))
                                            .flatMap(
                                                savedStatus -> {
                                                  return postDraftRepo
                                                      .save(save)
                                                      .map(saved -> postAssembler.fromDraft(saved))
                                                      .map(
                                                          result ->
                                                              Result.build(
                                                                  result, StatusCode.SAVE_SUCCESS));
                                                });
                                      });
                            } else {
                              return Mono.just(Result.build(StatusCode.SAVE_FAILED));
                            }
                          });
                });
  }

  @Transactional
  @Override
  public Mono<Result<PostDto>> delete(AuthDto authDto, PostDto dto) {
    return postRepo
        .findById(dto.getId())
        .flatMap(
            (Function<Post, Mono<Result<PostDto>>>)
                post -> {
                  post.setDeletedBy(authDto.getId());
                  post.setDeletedAt(new Date());
                  post.setStatus(
                      Arrays.asList(PostStatus.UNPUBLISHED).stream().collect(Collectors.toSet()));
                  return postRepo
                      .save(post)
                      .flatMap(
                          saved -> {
                            return postDraftRepo
                                .deleteById(post.getId())
                                .map(
                                    result -> {
                                      return Result.build(dto, StatusCode.DELETE_SUCCESS);
                                    });
                          });
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
  public Mono<Result<PostDto>> find(AuthDto session, IdRequest idRequest) {
    return postRepo
        .find(session, idRequest)
        .map(postAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }

  @Override
  public Mono<Result<PostDto>> findDraft(AuthDto authDto, IdRequest idRequest) {
    return postDraftRepo
        .find(authDto, idRequest)
        .map(postAssembler::fromDraft)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }

  @Transactional
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
                        exitingPost.setStatus(
                            new HashSet<>(Arrays.asList(PostStatus.PUBLISHED.name())));
                        Set<IdRequest> ids =
                            exitingPost.getCategories().stream()
                                .map(category -> IdRequest.builder().id(category.getId()).build())
                                .collect(Collectors.toSet());
                        return categoryService
                            .findByIds(session, ids)
                            .flatMap(
                                categoryDtos -> {
                                  categoryDtos.forEach(
                                      categoryDto -> {
                                        exitingPost
                                            .getCategories()
                                            .forEach(
                                                category -> {
                                                  if (category
                                                      .getId()
                                                      .equals(categoryDto.getId())) {
                                                    category.setName(categoryDto.getName());
                                                  }
                                                });
                                      });
                                  return postRepo
                                      .save(exitingPost)
                                      .flatMap(
                                          saved -> {
                                            postDraft.setStatus(
                                                new HashSet<>(Arrays.asList(PostStatus.PUBLISHED)));
                                            return postDraftRepo
                                                .save(postDraft)
                                                .map(result -> postAssembler.fromEntity(saved))
                                                .map(
                                                    returned ->
                                                        Result.build(
                                                            returned, StatusCode.SAVE_SUCCESS));
                                          });
                                });
                      });
            });
  }

  @Override
  public Mono<Result<PostDto>> deleteDraft(AuthDto session, PostDto dto) {
    return postDraftRepo.deleteById(IdRequest.builder().id(dto.getId()).build())
            .map(postDraft -> {
                return Result.build(postAssembler.fromDraft(postDraft), StatusCode.DELETE_SUCCESS);
            });
  }
}
