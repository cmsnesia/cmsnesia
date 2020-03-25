package com.cmsnesia.domain.repository;

import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.repository.custom.PostRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PostRepo extends ReactiveMongoRepository<Post, String>, PostRepoCustom {}
