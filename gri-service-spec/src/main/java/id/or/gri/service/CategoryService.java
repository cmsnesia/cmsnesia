package id.or.gri.service;

import id.or.gri.model.AuthDto;
import id.or.gri.model.CategoryDto;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface CategoryService extends BaseService<CategoryDto> {

    Mono<Boolean> exists(AuthDto session, Set<String> ids);

    Mono<CategoryDto> findById(AuthDto session, String id);

}
