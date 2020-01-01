package id.or.gri.service.repository.custom;

import id.or.gri.domain.Category;
import id.or.gri.model.AuthDto;
import id.or.gri.model.CategoryDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface CategoryRepoCustom {

    Flux<Category> find(AuthDto authDto, CategoryDto dto, Pageable pageable);

    Mono<Boolean> exists(Set<String> ids);

}
