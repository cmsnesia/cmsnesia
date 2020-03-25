package com.cmsnesia.domain.repository.custom;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.Category;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.request.IdRequest;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
public class CategoryRepoCustomImpl implements CategoryRepoCustom {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<Category> find(Session session, IdRequest id) {
    Query query = buildQuery(session, CategoryDto.builder().id(id.getId()).build());
    return reactiveMongoTemplate.findOne(query, Category.class);
  }

  @Override
  public Flux<Category> find(Session session, Set<IdRequest> ids) {
    Query query = new Query();
    query.addCriteria(
        Criteria.where("id").in(ids.stream().map(IdRequest::getId).collect(Collectors.toSet())));
    query.addCriteria(Criteria.where("applications.id").in(Session.applicationIds(session)));
    return reactiveMongoTemplate.find(query, Category.class);
  }

  @Override
  public Flux<Category> find(Session session, CategoryDto dto, Pageable pageable) {
    Query query = buildQuery(session, dto);
    if (pageable.isPaged()) {
      query.with(pageable);
    }
    return reactiveMongoTemplate.find(query, Category.class);
  }

  @Override
  public Mono<Long> countFind(Session session, CategoryDto dto) {
    Query query = buildQuery(session, dto);
    return reactiveMongoTemplate.count(query, Category.class);
  }

  @Override
  public Mono<Boolean> exists(Session session, Set<String> ids) {
    Query query = new Query();
    query.addCriteria(Criteria.where("id").in(ids));
    query.addCriteria(Criteria.where("applications.id").in(Session.applicationIds(session)));
    query.addCriteria(Criteria.where("deletedAt").exists(false));
    return reactiveMongoTemplate
        .count(query, Category.class)
        .map(count -> count == null ? false : count == ids.size());
  }

  @Override
  public Mono<Boolean> exists(Session session, String id, String name) {
    Query query = new Query();
    if (!StringUtils.isEmpty(id)) {
      query.addCriteria(Criteria.where("id").ne(id));
    }
    query.addCriteria(Criteria.where("name").is(name));
    query.addCriteria(Criteria.where("applications.id").in(Session.applicationIds(session)));
    query.addCriteria(Criteria.where("deletedAt").exists(false));
    return reactiveMongoTemplate.exists(query, Category.class);
  }

  private Query buildQuery(Session session, CategoryDto dto) {

    Query query = new Query();

    query.with(Sort.by(Sort.Order.desc("createdAt")));

    query.addCriteria(Criteria.where("applications.id").in(Session.applicationIds(session)));

    query.addCriteria(Criteria.where("deletedAt").exists(false));

    if (!StringUtils.isEmpty(dto.getId())) {
      query.addCriteria(Criteria.where("id").is(dto.getId()));
    } else {
      if (!StringUtils.isEmpty(dto.getName())) {
        Pattern regex = Pattern.compile(dto.getName(), Pattern.CASE_INSENSITIVE);
        query.addCriteria(Criteria.where("name").regex(regex));
      }
    }

    return query;
  }
}
