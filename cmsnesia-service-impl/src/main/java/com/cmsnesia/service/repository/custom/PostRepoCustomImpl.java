package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Post;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

public class PostRepoCustomImpl implements PostRepoCustom {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

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

    private Query buildQuery(AuthDto authDto, PostDto dto) {
        Query query = new Query();

        query.addCriteria(Criteria.where("deletedAt").exists(false));

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
