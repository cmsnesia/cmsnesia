package com.cmsnesia.service.repository;

import com.cmsnesia.domain.Profile;
import com.cmsnesia.service.repository.custom.ProfileRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProfileRepo extends ReactiveMongoRepository<Profile, String>, ProfileRepoCustom {}
