package com.cmsnesia.service.command.post;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.model.Author;
import com.cmsnesia.domain.model.enums.PostStatus;
import com.cmsnesia.domain.repository.CategoryRepo;
import com.cmsnesia.domain.repository.PostDraftRepo;
import com.cmsnesia.domain.repository.PostRepo;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.command.AbstractCommand;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PublishCommand extends AbstractCommand<IdRequest, Result<PostDto>> {

  private final PostAssembler postAssembler;
  private final PostRepo postRepo;
  private final PostDraftRepo postDraftRepo;
  private final CategoryRepo categoryRepo;

  @Override
  public Publisher<Result<PostDto>> execute(Session session, IdRequest id) {
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
                              .find(session, id.getId(), id.getId())
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
                                    return categoryRepo
                                        .find(
                                            session,
                                            ids.stream()
                                                .map(IdRequest::getId)
                                                .collect(Collectors.toSet()))
                                        .collectList()
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
}
