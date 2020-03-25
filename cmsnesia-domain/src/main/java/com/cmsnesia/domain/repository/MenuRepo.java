package com.cmsnesia.domain.repository;

import com.cmsnesia.domain.Menu;
import com.cmsnesia.domain.repository.custom.MenuRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MenuRepo extends ReactiveMongoRepository<Menu, String>, MenuRepoCustom {}
