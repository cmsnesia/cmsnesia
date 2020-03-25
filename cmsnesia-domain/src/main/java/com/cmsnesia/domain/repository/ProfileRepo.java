package com.cmsnesia.domain.repository;

import com.cmsnesia.domain.Profile;
import com.cmsnesia.domain.repository.custom.ProfileRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProfileRepo extends ReactiveMongoRepository<Profile, String>, ProfileRepoCustom {}
