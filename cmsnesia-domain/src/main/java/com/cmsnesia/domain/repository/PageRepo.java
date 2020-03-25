package com.cmsnesia.domain.repository;

import com.cmsnesia.domain.Page;
import com.cmsnesia.domain.repository.custom.PageRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PageRepo extends ReactiveMongoRepository<Page, String>, PageRepoCustom {}
