package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Category;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface CategoryRepoCustom {

    Flux<Category> find(AuthDto authDto, CategoryDto dto, Pageable pageable);

    Mono<Long> countFind(AuthDto authDto, CategoryDto dto);

    Mono<Boolean> exists(Set<String> ids);

}
