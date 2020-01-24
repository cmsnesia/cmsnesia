package id.or.gri.service;

import id.or.gri.model.AuthDto;
import id.or.gri.model.PostDto;
import id.or.gri.model.request.IdRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService extends BaseService<PostDto> {

    Mono<PostDto> publish(AuthDto session, IdRequest id);

    Flux<PostDto> findDraft(AuthDto authDto, PostDto dto, Pageable pageable);
}
