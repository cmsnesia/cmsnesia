package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Page;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.AuthorDto;
import com.cmsnesia.model.PageDto;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.util.Sessions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PageRepoCustomImpl implements PageRepoCustom {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<Page> find(AuthDto authDto, IdRequest id) {
    Query query = buildQuery(authDto, PageDto.builder().id(id.getId()).build());
    return reactiveMongoTemplate.findOne(query, Page.class);
  }

  @Override
  public Flux<Page> find(AuthDto authDto, PageDto dto, Pageable pageable) {
    Query query = buildQuery(authDto, dto);
    if (pageable.isPaged()) {
      query.with(pageable);
    }
    return reactiveMongoTemplate.find(query, Page.class);
  }

  @Override
  public Mono<Long> countFind(AuthDto authDto, PageDto dto) {
    Query query = buildQuery(authDto, dto);
    return reactiveMongoTemplate.count(query, Page.class);
  }

  @Override
  public Mono<Page> findAbout(AuthDto session) {
    Query query = new Query();
    query.addCriteria(Criteria.where("applications.id").in(Sessions.applicationIds(session)));
    query.addCriteria(Criteria.where("name").is("About"));
    return reactiveMongoTemplate.findOne(query, Page.class);
  }

  @Override
  public Mono<Boolean> exists(AuthDto session, String id, String name) {
    Query query = new Query();
    if (!StringUtils.isEmpty(id)) {
      query.addCriteria(Criteria.where("id").ne(id));
    }
    query.addCriteria(Criteria.where("name").is(name));
    query.addCriteria(Criteria.where("applications.id").in(Sessions.applicationIds(session)));
    return reactiveMongoTemplate.exists(query, Page.class);
  }

  private Query buildQuery(AuthDto session, PageDto dto) {

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

    return query;
  }
}
