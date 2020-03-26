package com.cmsnesia.service.command.post;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PostAssembler;
import com.cmsnesia.domain.repository.PostDraftRepo;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.service.command.AbstractCommand;
import com.cmsnesia.service.command.Command;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FindAllDraft extends AbstractCommand<Command.PageableRequest<PostDto>, Page<PostDto>> {

  private final PostAssembler postAssembler;
  private final PostDraftRepo postDraftRepo;

  @Override
  public Publisher<Page<PostDto>> execute(Session session, PageableRequest<PostDto> request) {
    PostDto dto = request.getData();
    Pageable pageable = request.getPageable();
    Mono<Long> count = postDraftRepo.countFind(session, dto);
    Mono<List<PostDto>> posts =
        postDraftRepo.find(session, dto, pageable).map(postAssembler::fromDraft).collectList();
    return Mono.zip(count, posts)
        .map(tuple -> new PageImpl<>(tuple.getT2(), pageable, tuple.getT1()));
  }
}
