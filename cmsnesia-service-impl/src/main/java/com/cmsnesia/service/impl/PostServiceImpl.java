package com.cmsnesia.service.impl;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.PostDraft;
import com.cmsnesia.domain.model.Author;
import com.cmsnesia.domain.model.enums.PostStatus;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.CategoryService;
import com.cmsnesia.service.command.CommandExecutor;
import com.cmsnesia.service.PostService;
import com.cmsnesia.domain.repository.PostDraftRepo;
import com.cmsnesia.domain.repository.PostRepo;
import com.cmsnesia.service.command.post.CreatePostCommand;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class PostServiceImpl implements PostService {

  private final PostAssembler postAssembler;
  private final PostRepo postRepo;
  private final PostDraftRepo postDraftRepo;
  private final CategoryService categoryService;

  private final CommandExecutor commandExecutor;

  @Override
  public Mono<Result<PostDto>> add(Session session, PostDto dto) {
    return Mono.from(commandExecutor.execute(CreatePostCommand.class, session, dto));
  }

  @Transactional
  @Override
  public Mono<Result<PostDto>> edit(Session session, PostDto dto) {
    return postRepo
        .exists(session, dto.getId(), dto.getTitle(), null)
        .flatMap(
            exists -> {
              if (!exists) {
                return postRepo
                    .find(session, dto.getId())
                    .flatMap(
                        post -> {
                          PostDraft postDraft = postAssembler.fromPost(post);
                          postDraft.setApplications(Session.applications(session));
                          return postDraftRepo.save(postDraft);
                        })
                    .flatMap(
                        postDraft -> {
                          PostDto postDto = postAssembler.fromDraft(postDraft);
                          return editDraft(session, postDto);
                        });
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }

  @Transactional
  @Override
  public Mono<Result<PostDto>> editDraft(Session session, PostDto dto) {
    return postRepo
        .exists(session, dto.getId(), dto.getTitle(), null)
        .flatMap(
            exists -> {
              if (!exists) {
                return postDraftRepo
                    .find(session, dto.getId())
                    .flatMap(
                        (Function<PostDraft, Mono<Result<PostDto>>>)
                            postDraft -> {
                              PostDraft save = postAssembler.fromPostDto(dto);

                              postDraft.setStatus(
                                  Arrays.asList(PostStatus.UNPUBLISHED.name()).stream()
                                      .collect(Collectors.toSet()));

                              save.audit(postDraft);

                              save.setModifiedBy(session.getId());
                              save.setModifiedAt(new Date());

                              Set<IdRequest> categoryIds =
                                  dto.getCategories().stream()
                                      .map(
                                          categoryDto ->
                                              IdRequest.builder().id(categoryDto.getId()).build())
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
                                                                      category.setName(
                                                                          categoryDto.getName());
                                                                    }
                                                                  });
                                                            });
                                                    return postRepo
                                                        .findAndModifyStatus(
                                                            session,
                                                            save.getId(),
                                                            new HashSet<>(
                                                                Arrays.asList(
                                                                    PostStatus.PUBLISHED,
                                                                    PostStatus.DRAFTED)))
                                                        .flatMap(
                                                            savedStatus -> {
                                                              return postDraftRepo
                                                                  .save(save)
                                                                  .map(
                                                                      saved ->
                                                                          postAssembler.fromDraft(
                                                                              saved))
                                                                  .map(
                                                                      result ->
                                                                          Result.build(
                                                                              result,
                                                                              StatusCode
                                                                                  .SAVE_SUCCESS));
                                                            });
                                                  });
                                        } else {
                                          return Mono.just(Result.build(StatusCode.SAVE_FAILED));
                                        }
                                      });
                            });
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }

  @Transactional
  @Override
  public Mono<Result<PostDto>> delete(Session authDto, PostDto dto) {
    return postRepo
        .exists(authDto, new HashSet<>(Arrays.asList(dto.getId())))
        .flatMap(
            exist -> {
              if (exist) {
                return postRepo
                    .find(authDto, dto.getId())
                    .flatMap(
                        (Function<Post, Mono<Result<PostDto>>>)
                            post -> {
                              post.setDeletedBy(authDto.getId());
                              post.setDeletedAt(new Date());
                              post.setStatus(
                                  Arrays.asList(PostStatus.UNPUBLISHED.name()).stream()
                                      .collect(Collectors.toSet()));
                              return postRepo
                                  .save(post)
                                  .flatMap(
                                      saved -> {
                                        return postDraftRepo
                                            .deleteById(post.getId())
                                            .map(
                                                result -> {
                                                  return Result.build(
                                                      dto, StatusCode.DELETE_SUCCESS);
                                                });
                                      });
                            });
              } else {
                return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
              }
            });
  }

  @Override
  public Mono<Page<PostDto>> find(Session authDto, PostDto dto, Pageable pageable) {
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
  public Mono<Page<PostDto>> findDraft(Session authDto, PostDto dto, Pageable pageable) {
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
  public Mono<Result<PostDto>> find(Session session, IdRequest idRequest) {
    return postRepo
        .find(session, idRequest.getId())
        .map(postAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }

  @Override
  public Mono<Result<PostDto>> findDraft(Session authDto, IdRequest idRequest) {
    return postDraftRepo
        .find(authDto, idRequest.getId())
        .map(postAssembler::fromDraft)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }

  @Transactional
  @Override
  public Mono<Result<PostDto>> publish(Session session, IdRequest id) {
    return postDraftRepo
        .exists(session, new HashSet<>(Arrays.asList(id.getId())))
        .flatMap(
            exists -> {
              if (exists) {
                return postDraftRepo
                    .find(session, id.getId())
                    .flatMap(
                        postDraft -> {
                          Post newPost = postAssembler.fromDto(postAssembler.fromDraft(postDraft));
                          newPost.setCreatedAt(new Date());
                          newPost.setCreatedBy(session.getId());
                          return postRepo
                              .find(session, id.getId())
                              .defaultIfEmpty(newPost)
                              .flatMap(
                                  post -> {
                                    Set<Author> authors =
                                        post.getAuthors() == null
                                            ? new HashSet<>()
                                            : post.getAuthors();
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
                                            .map(
                                                category ->
                                                    IdRequest.builder()
                                                        .id(category.getId())
                                                        .build())
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
                                                                category.setName(
                                                                    categoryDto.getName());
                                                              }
                                                            });
                                                  });
                                              exitingPost.setApplications(
                                                  Session.applications(session));
                                              return postRepo
                                                  .save(exitingPost)
                                                  .flatMap(
                                                      saved -> {
                                                        postDraft.setStatus(
                                                            new HashSet<>(
                                                                Arrays.asList(
                                                                    PostStatus.PUBLISHED.name())));
                                                        return postDraftRepo
                                                            .save(postDraft)
                                                            .map(
                                                                result ->
                                                                    postAssembler.fromEntity(saved))
                                                            .map(
                                                                returned ->
                                                                    Result.build(
                                                                        returned,
                                                                        StatusCode.SAVE_SUCCESS));
                                                      });
                                            });
                                  });
                        });
              } else {
                return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
              }
            });
  }

  @Override
  public Mono<Result<PostDto>> deleteDraft(Session session, PostDto dto) {
    return postDraftRepo
        .deleteById(session, dto.getId())
        .flatMap(
            postDraft ->
                postRepo
                    .findAndModifyStatus(
                        session, dto.getId(), new HashSet<>(Arrays.asList(PostStatus.PUBLISHED)))
                    .map(
                        post ->
                            Result.build(
                                postAssembler.fromDraft(postDraft), StatusCode.DELETE_SUCCESS))
                    .defaultIfEmpty(
                        Result.build(
                            postAssembler.fromDraft(postDraft), StatusCode.DELETE_FAILED)));
  }
}
