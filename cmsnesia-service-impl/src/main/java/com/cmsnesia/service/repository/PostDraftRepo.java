package com.cmsnesia.service.repository;

import cmsnesia.domain.PostDraft;
import com.cmsnesia.service.repository.custom.PostDraftRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PostDraftRepo extends ReactiveMongoRepository<PostDraft, String>, PostDraftRepoCustom {
}
