package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Menu;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.MenuDto;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.util.Sessions;
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
public class MenuRepoCustomImpl implements MenuRepoCustom {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<Menu> find(AuthDto authDto, IdRequest id) {
    Query query = buildQuery(authDto, MenuDto.builder().id(id.getId()).build());
    return reactiveMongoTemplate.findOne(query, Menu.class);
  }

  @Override
  public Flux<Menu> find(AuthDto authDto, MenuDto dto, Pageable pageable) {
    Query query = buildQuery(authDto, dto);
    if (pageable.isPaged()) {
      query.with(pageable);
    }
    return reactiveMongoTemplate.find(query, Menu.class);
  }

  @Override
  public Mono<Long> countFind(AuthDto authDto, MenuDto dto) {
    Query query = buildQuery(authDto, dto);
    return reactiveMongoTemplate.count(query, Menu.class);
  }

  @Override
  public Mono<Boolean> exists(AuthDto session, String id, String name) {
    Query query = new Query();
    if (!StringUtils.isEmpty(id)) {
      query.addCriteria(Criteria.where("id").ne(id));
    }
    query.addCriteria(Criteria.where("name").is(name));
    query.addCriteria(Criteria.where("applications.id").in(Sessions.applicationIds(session)));
    query.addCriteria(Criteria.where("deletedAt").exists(false));
    return reactiveMongoTemplate.exists(query, Menu.class);
  }

  private Query buildQuery(AuthDto session, MenuDto dto) {

    Query query = new Query();

    query.with(Sort.by(Sort.Order.desc("createdAt")));

    query.addCriteria(Criteria.where("applications.id").in(Sessions.applicationIds(session)));

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
