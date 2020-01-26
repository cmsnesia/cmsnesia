package com.cmsnesia.service.repository;

import cmsnesia.domain.Tag;
import com.cmsnesia.service.repository.custom.TagRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

@Deprecated
public interface TagRepo extends ReactiveMongoRepository<Tag, String>, TagRepoCustom {
}
