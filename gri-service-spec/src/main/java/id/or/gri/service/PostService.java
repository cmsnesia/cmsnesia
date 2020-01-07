package id.or.gri.service;

import id.or.gri.model.AuthDto;
import id.or.gri.model.PostDto;
import id.or.gri.model.request.IdRequest;
import reactor.core.publisher.Mono;

public interface PostService extends BaseService<PostDto> {

    Mono<PostDto> publish(AuthDto session, IdRequest id);

}
