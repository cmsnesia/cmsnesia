package id.or.gri.service.repository.custom;

import id.or.gri.domain.Category;
import id.or.gri.model.AuthDto;
import id.or.gri.model.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.regex.Pattern;

public class CategoryRepoCustomImpl implements CategoryRepoCustom {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Flux<Category> find(AuthDto authDto, CategoryDto dto, Pageable pageable) {
        Query query = buildQuery(authDto, dto);
        if (pageable.isPaged()) {
            query.with(pageable);
        }
        return reactiveMongoTemplate.find(query, Category.class);
    }

    @Override
    public Mono<Boolean> exists(Set<String> ids) {
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(
                Criteria.where("id").all(ids)
        ));
        return reactiveMongoTemplate.exists(query, Category.class);
    }

    private Query buildQuery(AuthDto authDto, CategoryDto dto) {
        Query query = new Query();

        query.addCriteria(new Criteria().orOperator(
                Criteria.where("deletedAt").exists(false),
                Criteria.where("deletedAt").is(null)
        ));

        if (!StringUtils.isEmpty(dto.getId())) {
            query.addCriteria(Criteria.where("id").is(dto.getId()));
        }

        if (!StringUtils.isEmpty(dto.getName())) {
            Pattern regex = Pattern.compile(dto.getName(), Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("name").regex(regex));
        }

        return query;
    }
}
