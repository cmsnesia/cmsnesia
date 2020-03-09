package com.cmsnesia.service.repository;

import com.cmsnesia.domain.CategoryGroup;
import com.cmsnesia.service.repository.custom.CategoryGroupRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryGroupRepo
        extends ReactiveMongoRepository<CategoryGroup, String>, CategoryGroupRepoCustom {
}
