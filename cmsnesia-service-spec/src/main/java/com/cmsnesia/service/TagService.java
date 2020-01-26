package com.cmsnesia.service;

import com.cmsnesia.model.TagDto;
import reactor.core.publisher.Mono;

import java.util.Set;

@Deprecated
public interface TagService extends BaseService<TagDto> {

    Mono<Boolean> exists(Set<String> ids);

}
