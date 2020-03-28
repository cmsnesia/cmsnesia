package com.cmsnesia.service.command.page;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PageAssembler;
import com.cmsnesia.domain.repository.PageRepo;
import com.cmsnesia.model.PageDto;
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
@Service("pageFindAllCommand")
public class FindAllCommand
    extends AbstractCommand<Command.PageableRequest<PageDto>, Page<PageDto>> {

  private final PageAssembler pageAssembler;
  private final PageRepo pageRepo;

  @Override
  public Publisher<Page<PageDto>> execute(Session session, PageableRequest<PageDto> request) {
    PageDto dto = request.getData();
    Pageable pageable = request.getPageable();
    Mono<Long> count = pageRepo.countFind(session, dto);
    Mono<List<PageDto>> pages =
        pageRepo
            .find(session, dto, pageable)
            .map(page -> pageAssembler.fromEntity(page))
            .collectList();
    return Mono.zip(count, pages)
        .map(tuple -> new PageImpl<>(tuple.getT2(), pageable, tuple.getT1()));
  }
}
