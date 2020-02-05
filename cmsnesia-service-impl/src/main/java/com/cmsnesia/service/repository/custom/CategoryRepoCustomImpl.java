package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Category;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.request.IdRequest;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CategoryRepoCustomImpl implements CategoryRepoCustom {

  @Autowired private ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<Category> find(AuthDto authDto, IdRequest id) {
    Query query = buildQuery(authDto, CategoryDto.builder().id(id.getId()).build());
    return reactiveMongoTemplate.findOne(query, Category.class);
  }

  @Override
  public Flux<Category> find(AuthDto authDto, CategoryDto dto, Pageable pageable) {
    Query query = buildQuery(authDto, dto);
    if (pageable.isPaged()) {
      query.with(pageable);
    }
    return reactiveMongoTemplate.find(query, Category.class);
  }

  @Override
  public Mono<Long> countFind(AuthDto authDto, CategoryDto dto) {
    Query query = buildQuery(authDto, dto);
    return reactiveMongoTemplate.count(query, Category.class);
  }

  @Override
  public Mono<Boolean> exists(Set<String> ids) {
    Query query = new Query();
    query.addCriteria(Criteria.where("id").in(ids));
    return reactiveMongoTemplate.count(query, Category.class)
            .map(count -> count == null ? false : count == ids.size());
  }

  private Query buildQuery(AuthDto authDto, CategoryDto dto) {
    Query query = new Query();

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
