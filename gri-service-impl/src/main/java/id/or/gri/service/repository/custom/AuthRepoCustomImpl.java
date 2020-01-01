package id.or.gri.service.repository.custom;

import id.or.gri.domain.Auth;
import id.or.gri.model.AuthDto;
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
        Query query = buildQuery(authDto, dto);
        if (pageable.isPaged()) {
            query.with(pageable);
        }
        return reactiveMongoTemplate.find(query, Auth.class);
    }

    @Override
    public Mono<Long> countFind(AuthDto authDto, AuthDto dto) {
        Query query = buildQuery(authDto, dto);
        return reactiveMongoTemplate.count(query, Auth.class);
    }

    private Query buildQuery(AuthDto authDto, AuthDto dto) {
        Query query = new Query();

        query.addCriteria(new Criteria().orOperator(
                Criteria.where("deletedAt").exists(false),
                Criteria.where("deletedAt").is(null)
        ));

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

        return query;
    }

}
