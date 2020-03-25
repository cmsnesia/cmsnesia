package com.cmsnesia.domain.repository;

import com.cmsnesia.domain.Category;
import com.cmsnesia.domain.repository.custom.CategoryRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryRepo
    extends ReactiveMongoRepository<Category, String>, CategoryRepoCustom {}
