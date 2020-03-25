package com.cmsnesia.domain.repository.custom;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.Event;
import com.cmsnesia.model.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class EventRepoCustomImpl implements EventRepoCustom {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<Event> find(Session session, String id) {
    Query query = buildQuery(session, EventDto.builder().id(id).build());
    return reactiveMongoTemplate.findOne(query, Event.class);
  }

  @Override
  public Flux<Event> find(Session session, Set<String> ids) {
    Query query = buildQuery(session, EventDto.builder().build());
    query.addCriteria(Criteria.where("id").in(ids));
    return reactiveMongoTemplate.find(query, Event.class);
  }

  @Override
  public Flux<Event> find(Session session, EventDto dto, Pageable pageable) {
    Query query = buildQuery(session, dto);
    if (pageable.isPaged()) {
      query.with(pageable);
    }
    return reactiveMongoTemplate.find(query, Event.class);
  }

  @Override
  public Mono<Long> countFind(Session session, EventDto dto) {
    Query query = buildQuery(session, dto);
    return reactiveMongoTemplate.count(query, Event.class);
  }

  @Override
  public Mono<Boolean> exists(Session session, Set<String> ids) {
    Query query = buildQuery(session, EventDto.builder().build());
    query.addCriteria(Criteria.where("id").in(ids));
    return reactiveMongoTemplate
        .count(query, Event.class)
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
      return reactiveMongoTemplate.exists(query, Event.class);
  }

  private Query buildQuery(Session session, EventDto dto) {

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
      if (!CollectionUtils.isEmpty(dto.getTypes())) {
        query.addCriteria(Criteria.where("types").in(dto.getTypes()));
      }
    }

    return query;
  }
}
