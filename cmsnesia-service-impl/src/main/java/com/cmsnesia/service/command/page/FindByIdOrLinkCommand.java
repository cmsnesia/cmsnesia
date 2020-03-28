package com.cmsnesia.service.command.page;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PageAssembler;
import com.cmsnesia.domain.repository.PageRepo;
import com.cmsnesia.model.PageDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.service.command.AbstractCommand;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("pageFindByIdOrLink")
public class FindByIdOrLinkCommand extends AbstractCommand<PageDto, Result<PageDto>> {

  private final PageAssembler pageAssembler;
  private final PageRepo pageRepo;

  @Override
  public Publisher<Result<PageDto>> execute(Session session, PageDto request) {
    return pageRepo
        .find(session, request.getId(), request.getLink())
        .map(page -> pageAssembler.fromEntity(page))
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }
}
