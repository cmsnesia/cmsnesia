package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.model.enums.PostStatus;
import com.cmsnesia.model.*;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.util.Sessions;
import com.mongodb.client.result.UpdateResult;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class PostRepoCustomImpl implements PostRepoCustom {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<Post> find(AuthDto authDto, IdRequest id) {
    Query query = buildQuery(authDto, PostDto.builder().id(id.getId()).build());
    Update update = new Update();
    update.inc("viewCount", 1);
    return reactiveMongoTemplate.findAndModify(query, update, Post.class);
  }

  @Override
  public Flux<Post> find(AuthDto authDto, PostDto dto, Pageable pageable) {
    Query query = buildQuery(authDto, dto);
    if (pageable.isPaged()) {
      query.with(pageable);
    }
    return reactiveMongoTemplate.find(query, Post.class);
  }

  @Override
  public Mono<Long> countFind(AuthDto authDto, PostDto dto) {
    Query query = buildQuery(authDto, dto);
    return reactiveMongoTemplate.count(query, Post.class);
  }

  @Override
  public Mono<UpdateResult> findAndModifyCategory(AuthDto authDto, CategoryDto categoryDto) {
    Query query = new Query();
    query.addCriteria(Criteria.where("categories.id").is(categoryDto.getId()));
    Update update = new Update();
    update.set("categories.$.name", categoryDto.getName());
    return reactiveMongoTemplate.updateMulti(query, update, Collection.class);
  }

  @Override
  public Mono<Post> findAndModifyStatus(AuthDto session, IdRequest id, Set<PostStatus> postStatus) {
    Query query = new Query();

    query.addCriteria(Criteria.where("id").is(id.getId()));
    query.addCriteria(Criteria.where("applications.id").is(Sessions.applicationIds(session)));
    return reactiveMongoTemplate
        .exists(query, Post.class)
        .flatMap(
            exist -> {
              if (exist) {
                Update update = new Update();
                update.set(
                    "status",
                    postStatus.stream().map(PostStatus::name).collect(Collectors.toSet()));
                return reactiveMongoTemplate.findAndModify(
                    query, update, FindAndModifyOptions.options().returnNew(true), Post.class);
              } else {
                return Mono.just(Post.builder().id(id.getId()).build());
              }
            });
  }

  @Override
  public Mono<Boolean> exists(AuthDto session, String id, String name) {
    Query query = new Query();
    if (!StringUtils.isEmpty(id)) {
      query.addCriteria(Criteria.where("id").ne(id));
    }
    query.addCriteria(Criteria.where("title").is(name));
    query.addCriteria(Criteria.where("applications.id").in(Sessions.applicationIds(session)));
    query.addCriteria(Criteria.where("deletedAt").exists(false));
    return reactiveMongoTemplate.exists(query, Post.class);
  }

  private Query buildQuery(AuthDto session, PostDto dto) {

    Query query = new Query();

    query.with(Sort.by(Sort.Order.desc("createdAt")));

    query.addCriteria(Criteria.where("applications.id").in(Sessions.applicationIds(session)));
    query.addCriteria(Criteria.where("deletedAt").exists(false));
    query.addCriteria(Criteria.where("status").is(Arrays.asList(PostStatus.PUBLISHED.name())));

    if (!StringUtils.isEmpty(dto.getId())) {
      query.addCriteria(Criteria.where("id").is(dto.getId()));
    } else {
      if (!StringUtils.isEmpty(dto.getTitle())) {
        Pattern regex = Pattern.compile(dto.getTitle(), Pattern.CASE_INSENSITIVE);
        query.addCriteria(Criteria.where("title").regex(regex));
      }

      if (!StringUtils.isEmpty(dto.getContent())) {
        Pattern regex = Pattern.compile(dto.getContent(), Pattern.CASE_INSENSITIVE);
        query.addCriteria(Criteria.where("content").regex(regex));
      }

      if (dto.getAuthors() != null && !dto.getAuthors().isEmpty()) {
        Set<String> names =
            dto.getAuthors().stream()
                .filter(authorDto -> StringUtils.hasText(authorDto.getName()))
                .map(AuthorDto::getName)
                .collect(Collectors.toSet());
        if (!names.isEmpty()) {
          query.addCriteria(Criteria.where("authors.name").in(names));
        }
      }

      if (dto.getTags() != null && !dto.getTags().isEmpty()) {
        Set<String> names =
            dto.getTags().stream()
                .filter(tagDto -> StringUtils.hasText(tagDto.getName()))
                .map(TagDto::getName)
                .collect(Collectors.toSet());
        if (!names.isEmpty()) {
          query.addCriteria(Criteria.where("tags.name").in(names));
        }
      }

      if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
        if (dto.getCategories().stream()
            .anyMatch(categoryDto -> !Objects.isNull(categoryDto.getId()))) {
          Set<String> ids =
              dto.getCategories().stream().map(CategoryDto::getId).collect(Collectors.toSet());
          query.addCriteria(Criteria.where("categories.id").in(ids));
        } else {
          Set<String> names =
              dto.getCategories().stream().map(CategoryDto::getName).collect(Collectors.toSet());
          query.addCriteria(Criteria.where("categories.name").in(names));
        }
      }
    }

    return query;
  }
}
