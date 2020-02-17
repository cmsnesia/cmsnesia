package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Auth;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class AuthRepoCustomImpl implements AuthRepoCustom {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<Auth> find(AuthDto authDto, IdRequest id) {
    Query query = buildQuery(authDto, AuthDto.builder().id(id.getId()).build(), null, null, null);
    return reactiveMongoTemplate.findOne(query, Auth.class);
  }

  @Override
  public Flux<Auth> find(AuthDto authDto, AuthDto dto, Pageable pageable) {
    Query query = buildQuery(authDto, dto, null, null, null);
    if (pageable.isPaged()) {
      query.with(pageable);
    }
    return reactiveMongoTemplate.find(query, Auth.class);
  }

  @Override
  public Mono<Long> countFind(AuthDto authDto, AuthDto dto) {
    Query query = buildQuery(authDto, dto, null, null, null);
    return reactiveMongoTemplate.count(query, Auth.class);
  }

  @Override
  public Mono<Auth> findByAccessTokenAndType(
      AuthDto authDto, String accessToken, String tokenType) {
    Query query = buildQuery(authDto, AuthDto.builder().build(), accessToken, null, tokenType);
    return reactiveMongoTemplate.findOne(query, Auth.class);
  }

  @Override
  public Mono<Auth> findByRefreshTokenAndTokenType(
      AuthDto authDto, String refreshToken, String tokenType) {
    Query query = buildQuery(authDto, AuthDto.builder().build(), null, refreshToken, tokenType);
    return reactiveMongoTemplate.findOne(query, Auth.class);
  }

  @Override
  public Mono<Auth> findByAccessTokenAndRefreshTokenAndTokenType(
      AuthDto authDto, TokenResponse token) {
    Query query =
        buildQuery(
            authDto,
            AuthDto.builder().build(),
            token.getAccessToken(),
            token.getRefreshToken(),
            token.getTokenType());
    return reactiveMongoTemplate.findOne(query, Auth.class);
  }

  private Query buildQuery(
      AuthDto authDto, AuthDto dto, String accessToken, String refreshToken, String tokenType) {
    Query query = new Query();

    query.with(Sort.by(Sort.Order.desc("createdAt")));

    query.addCriteria(Criteria.where("deletedAt").exists(false));

    if (!StringUtils.isEmpty(dto.getId())) {
      query.addCriteria(Criteria.where("id").is(dto.getId()));
      if (!StringUtils.isEmpty(dto.getUsername())) {
        query.addCriteria(Criteria.where("username").is(dto.getUsername()));
      }
    } else if (!StringUtils.isEmpty(dto.getUsername())) {
      query.addCriteria(Criteria.where("username").is(dto.getUsername()));
    } else {
      if (!StringUtils.isEmpty(dto.getFullName())) {
        Pattern regex = Pattern.compile(dto.getFullName(), Pattern.CASE_INSENSITIVE);
        query.addCriteria(Criteria.where("fullName").regex(regex));
      }
      if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
        query.addCriteria(Criteria.where("roles").in(dto.getRoles()));
      }
      if (dto.getEmails() != null && !dto.getEmails().isEmpty()) {
        query.addCriteria(Criteria.where("emails").in(dto.getEmails()));
      }
    }
    if (!StringUtils.isEmpty(accessToken)
        && !StringUtils.isEmpty(refreshToken)
        && !StringUtils.isEmpty(tokenType)) {
      query.addCriteria(
          new Criteria()
              .andOperator(
                  Criteria.where("tokens.accessToken").is(accessToken),
                  Criteria.where("tokens.refreshToken").is(refreshToken),
                  Criteria.where("tokens.tokenType").is(tokenType)));
    } else if (!StringUtils.isEmpty(accessToken)) {
      query.addCriteria(
          new Criteria()
              .andOperator(
                  Criteria.where("tokens.accessToken").is(accessToken),
                  Criteria.where("tokens.tokenType").is(tokenType)));
    } else if (!StringUtils.isEmpty(refreshToken)) {
      query.addCriteria(
          new Criteria()
              .andOperator(
                  Criteria.where("tokens.refreshToken").is(refreshToken),
                  Criteria.where("tokens.tokenType").is(tokenType)));
    }

    return query;
  }
}
