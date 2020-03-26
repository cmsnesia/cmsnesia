package com.cmsnesia.service.command.post;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.repository.PostRepo;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.service.command.AbstractCommand;
import com.cmsnesia.service.command.Command;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
@Service
public class FindAllCommand
    extends AbstractCommand<Command.PageableRequest<PostDto>, Page<PostDto>> {

  private final PostAssembler postAssembler;
  private final PostRepo postRepo;

  @Override
  public Mono<Page<PostDto>> execute(Session session, PageableRequest<PostDto> request) {
    PostDto dto = request.getData();
    Pageable pageable = request.getPageable();
    Mono<Long> count = postRepo.countFind(session, dto);
    Mono<List<PostDto>> posts =
        postRepo.find(session, dto, pageable).map(postAssembler::fromEntity).collectList();
    return Mono.zip(count, posts)
        .map(tuple -> new PageImpl<>(tuple.getT2(), pageable, tuple.getT1()));
  }
}
