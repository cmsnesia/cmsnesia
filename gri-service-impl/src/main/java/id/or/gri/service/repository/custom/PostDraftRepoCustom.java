package id.or.gri.service.repository.custom;

import id.or.gri.domain.PostDraft;
import id.or.gri.model.AuthDto;
import id.or.gri.model.PostDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostDraftRepoCustom {

    Flux<PostDraft> find(AuthDto authDto, PostDto dto, Pageable pageable);

    Mono<Long> countFind(AuthDto authDto, PostDto dto);

}
