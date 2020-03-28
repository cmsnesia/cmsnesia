package com.cmsnesia.service.command.page;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.PageAssembler;
import com.cmsnesia.domain.Page;
import com.cmsnesia.domain.model.Author;
import com.cmsnesia.domain.repository.PageRepo;
import com.cmsnesia.model.PageDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.service.command.AbstractCommand;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("pageCreateCommand")
public class CreateCommand extends AbstractCommand<PageDto, Result<PageDto>> {

  private final PageAssembler pageAssembler;
  private final PageRepo pageRepo;

  @Override
  public Publisher<Result<PageDto>> execute(Session session, PageDto dto) {
    return pageRepo
        .exists(session, null, dto.getName(), dto.getLink())
        .flatMap(
            exists -> {
              if (!exists) {
                Page page = pageAssembler.fromDto(dto);
                page.setId(UUID.randomUUID().toString());
                page.setCreatedBy(session.getId());
                page.setCreatedAt(new Date());
                page.setAuthors(
                    Arrays.asList(
                            Author.builder()
                                .name(session.getFullName())
                                .modifiedAt(new Date())
                                .build())
                        .stream()
                        .collect(Collectors.toSet()));
                page.setApplications(Session.applications(session));
                return pageRepo
                    .save(page)
                    .map(pageAssembler::fromEntity)
                    .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }
}
