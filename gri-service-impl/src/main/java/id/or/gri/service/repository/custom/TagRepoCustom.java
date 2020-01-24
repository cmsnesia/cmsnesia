package id.or.gri.service.repository.custom;

import id.or.gri.domain.Tag;
import id.or.gri.model.AuthDto;
import id.or.gri.model.TagDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Deprecated
public interface TagRepoCustom {

    Flux<Tag> find(AuthDto authDto, TagDto dto, Pageable pageable);

    Mono<Boolean> exists(Set<String> ids);

}
