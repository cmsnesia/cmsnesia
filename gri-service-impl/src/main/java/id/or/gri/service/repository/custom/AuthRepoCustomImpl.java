package id.or.gri.service.repository.custom;

import id.or.gri.domain.Auth;
import id.or.gri.domain.model.Token;
import id.or.gri.model.AuthDto;
import id.or.gri.model.response.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

public class AuthRepoCustomImpl implements AuthRepoCustom {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

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
    public Mono<Auth> findByAccessTokenAndType(AuthDto authDto, String accessToken, String tokenType) {
        Query query = buildQuery(authDto, AuthDto.builder().build(), accessToken, null, tokenType);
        return reactiveMongoTemplate.findOne(query, Auth.class);
    }

    @Override
    public Mono<Auth> findByRefreshTokenAndTokenType(AuthDto authDto, String refreshToken, String tokenType) {
        Query query = buildQuery(authDto, AuthDto.builder().build(), null, refreshToken, tokenType);
        return reactiveMongoTemplate.findOne(query, Auth.class);
    }

    @Override
    public Mono<Auth> findByAccessTokenAndRefreshTokenAndTokenType(AuthDto authDto, TokenResponse token) {
        Query query = buildQuery(authDto, AuthDto.builder().build(), token.getAccessToken(), token.getRefreshToken(), token.getTokenType());
        return reactiveMongoTemplate.findOne(query, Auth.class);
    }

    private Query buildQuery(AuthDto authDto, AuthDto dto, String accessToken, String refreshToken, String tokenType) {
        Query query = new Query();

        query.addCriteria(Criteria.where("deletedAt").exists(false));

        if (!StringUtils.isEmpty(dto.getUsername())) {
            query.addCriteria(Criteria.where("username").is(dto.getUsername()));
        }
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
        if (!StringUtils.isEmpty(accessToken) && !StringUtils.isEmpty(refreshToken) && !StringUtils.isEmpty(tokenType)) {
            query.addCriteria(new Criteria().andOperator(
                    Criteria.where("tokens.accessToken").is(accessToken),
                    Criteria.where("tokens.refreshToken").is(refreshToken),
                    Criteria.where("tokens.tokenType").is(tokenType)
            ));
        } else if (!StringUtils.isEmpty(accessToken)) {
            query.addCriteria(new Criteria().andOperator(
                    Criteria.where("tokens.accessToken").is(accessToken),
                    Criteria.where("tokens.tokenType").is(tokenType)
            ));
        } else if (!StringUtils.isEmpty(refreshToken)) {
            query.addCriteria(new Criteria().andOperator(
                    Criteria.where("tokens.refreshToken").is(refreshToken),
                    Criteria.where("tokens.tokenType").is(tokenType)
            ));
        }

        return query;
    }

}
