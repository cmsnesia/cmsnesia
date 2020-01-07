package id.or.gri.service.repository.custom;

import id.or.gri.domain.PostDraft;
import id.or.gri.model.AuthDto;
import id.or.gri.model.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

public class PostDraftRepoCustomImpl implements PostDraftRepoCustom {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Flux<PostDraft> find(AuthDto authDto, PostDto dto, Pageable pageable) {
        Query query = buildQuery(authDto, dto);
        if (pageable.isPaged()) {
            query.with(pageable);
        }
        return reactiveMongoTemplate.find(query, PostDraft.class);
    }

    @Override
    public Mono<Long> countFind(AuthDto authDto, PostDto dto) {
        Query query = buildQuery(authDto, dto);
        return reactiveMongoTemplate.count(query, PostDraft.class);
    }

    private Query buildQuery(AuthDto authDto, PostDto dto) {
        Query query = new Query();

        query.addCriteria(new Criteria().orOperator(
                Criteria.where("deletedAt").exists(false),
                Criteria.where("deletedAt").is(null)
        ));

        if (!StringUtils.isEmpty(dto.getId())) {
            query.addCriteria(Criteria.where("id").is(dto.getId()));
        }

        if (!StringUtils.isEmpty(dto.getTitle())) {
            Pattern regex = Pattern.compile(dto.getTitle(), Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("title").regex(regex));
        }

        if (!StringUtils.isEmpty(dto.getContent())) {
            Pattern regex = Pattern.compile(dto.getContent(), Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("content").regex(regex));
        }

        return query;
    }


}
