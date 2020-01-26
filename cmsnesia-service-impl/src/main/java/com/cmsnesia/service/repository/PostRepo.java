package com.cmsnesia.service.repository;

import cmsnesia.domain.Post;
import com.cmsnesia.service.repository.custom.PostRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PostRepo extends ReactiveMongoRepository<Post, String>, PostRepoCustom {
}
