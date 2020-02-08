package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.model.enums.PostStatus;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.request.IdRequest;
import com.mongodb.client.result.UpdateResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    return reactiveMongoTemplate.findOne(query, Post.class);
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

  private Query buildQuery(AuthDto authDto, PostDto dto) {
    Query query = new Query();

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
        query.addCriteria(Criteria.where("authors").in(dto.getAuthors()));
      }
    }

    return query;
  }
}
