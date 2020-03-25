package com.cmsnesia.domain.repository;

import com.cmsnesia.domain.CategoryGroup;
import com.cmsnesia.domain.repository.custom.CategoryGroupRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryGroupRepo
    extends ReactiveMongoRepository<CategoryGroup, String>, CategoryGroupRepoCustom {}
