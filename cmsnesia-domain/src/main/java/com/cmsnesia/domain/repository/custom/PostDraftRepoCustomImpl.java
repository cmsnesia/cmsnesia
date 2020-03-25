package com.cmsnesia.domain.repository.custom;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.PostDraft;
import com.cmsnesia.domain.model.enums.PostStatus;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.request.IdRequest;

import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class PostDraftRepoCustomImpl implements PostDraftRepoCustom {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<PostDraft> find(Session session, IdRequest id) {
    Query query = buildQuery(session, PostDto.builder().id(id.getId()).build());
    return reactiveMongoTemplate.findOne(query, PostDraft.class);
  }

  @Override
  public Flux<PostDraft> find(Session session, PostDto dto, Pageable pageable) {
    Query query = buildQuery(session, dto);
    if (pageable.isPaged()) {
      query.with(pageable);
    }
    return reactiveMongoTemplate.find(query, PostDraft.class);
  }

  @Override
  public Mono<Long> countFind(Session session, PostDto dto) {
    Query query = buildQuery(session, dto);
    return reactiveMongoTemplate.count(query, PostDraft.class);
  }

  @Override
  public Mono<PostDraft> deleteById(Session session, IdRequest idRequest) {
    Query query = new Query(Criteria.where("id").is(idRequest.getId()));
    query.addCriteria(Criteria.where("applications.id").in(Session.applicationIds(session)));
    return reactiveMongoTemplate.findAndRemove(query, PostDraft.class);
  }

  @Override
  public Mono<Boolean> exists(Session session, String id, String name) {
    Query query = new Query();
    if (!StringUtils.isEmpty(id)) {
      query.addCriteria(Criteria.where("id").is(id));
    }
    query.addCriteria(Criteria.where("title").is(name));
    query.addCriteria(Criteria.where("applications.id").in(Session.applicationIds(session)));
    query.addCriteria(Criteria.where("deletedAt").exists(false));
    return reactiveMongoTemplate.exists(query, PostDraft.class);
  }

  @Override
  public Mono<Boolean> exists(Session session, Set<String> ids) {
    Query query = new Query();
    query.addCriteria(Criteria.where("id").in(ids));
    query.addCriteria(Criteria.where("applications.id").in(Session.applicationIds(session)));
    query.addCriteria(Criteria.where("deletedAt").exists(false));
    return reactiveMongoTemplate.exists(query, PostDraft.class);
  }

  private Query buildQuery(Session session, PostDto dto) {

    Query query = new Query();

    query.with(Sort.by(Sort.Order.desc("createdAt")));

    query.addCriteria(Criteria.where("applications.id").in(Session.applicationIds(session)));
    query.addCriteria(Criteria.where("deletedAt").exists(false));
    query.addCriteria(Criteria.where("status").is(PostStatus.UNPUBLISHED));

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
    }

    return query;
  }
}
