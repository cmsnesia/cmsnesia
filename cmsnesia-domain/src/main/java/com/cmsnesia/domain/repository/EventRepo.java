package com.cmsnesia.domain.repository;

import com.cmsnesia.domain.Event;
import com.cmsnesia.domain.repository.custom.EventRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EventRepo extends ReactiveMongoRepository<Event, String>, EventRepoCustom {}
