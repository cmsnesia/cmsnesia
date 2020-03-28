package com.cmsnesia.service.command.page;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PageAssembler;
import com.cmsnesia.domain.Page;
import com.cmsnesia.domain.repository.PageRepo;
import com.cmsnesia.model.PageDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.service.command.AbstractCommand;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Date;

@RequiredArgsConstructor
@Service("pageDeleteCommand")
public class DeleteCommand extends AbstractCommand<PageDto, Result<PageDto>> {

  private final PageAssembler pageAssembler;
  private final PageRepo pageRepo;

  @Override
  public Publisher<Result<PageDto>> execute(Session session, PageDto dto) {
    return pageRepo
        .find(session, dto.getId(), dto.getLink())
        .defaultIfEmpty(Page.builder().build())
        .flatMap(
            page -> {
              if (StringUtils.isEmpty(page.getId())) {
                return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
              } else {
                page.setDeletedAt(new Date());
                page.setDeletedBy(session.getId());
                return pageRepo
                    .save(page)
                    .map(pageAssembler::fromEntity)
                    .map(result -> Result.build(result, StatusCode.DELETE_SUCCESS));
              }
            });
  }
}
