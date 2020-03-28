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
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service("pageEditCommand")
public class EditCommand extends AbstractCommand<PageDto, Result<PageDto>> {

  private final PageAssembler pageAssembler;
  private final PageRepo pageRepo;

  @Override
  public Publisher<Result<PageDto>> execute(Session session, PageDto dto) {
    return pageRepo
        .exists(session, dto.getId(), dto.getName(), dto.getLink())
        .flatMap(
            exists -> {
              if (!exists) {
                return pageRepo
                    .find(session, dto.getId(), dto.getLink())
                    .defaultIfEmpty(Page.builder().build())
                    .flatMap(
                        page -> {
                          if (StringUtils.isEmpty(page.getId())) {
                            return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
                          } else {
                            Page save = pageAssembler.fromDto(dto);
                            save.audit(page);
                            Set<Author> authors =
                                page.getAuthors() == null ? new HashSet<>() : page.getAuthors();
                            if (authors.stream()
                                .map(Author::getName)
                                .noneMatch(name -> name.equals(session.getFullName()))) {
                              Author author = new Author();
                              author.setName(session.getFullName());
                              author.setModifiedAt(new Date());
                              authors.add(author);
                            }
                            save.setAuthors(authors);
                            return pageRepo
                                .save(save)
                                .map(
                                    result ->
                                        Result.build(
                                            pageAssembler.fromEntity(result),
                                            StatusCode.SAVE_SUCCESS));
                          }
                        });
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }
}
