package com.cmsnesia.service.repository.custom;

import cmsnesia.domain.Tag;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.TagDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Deprecated
public interface TagRepoCustom {

    Flux<Tag> find(AuthDto authDto, TagDto dto, Pageable pageable);

    Mono<Boolean> exists(Set<String> ids);

}
