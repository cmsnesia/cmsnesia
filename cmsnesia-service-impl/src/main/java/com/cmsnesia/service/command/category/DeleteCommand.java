package com.cmsnesia.service.command.category;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.CategoryAssembler;
import com.cmsnesia.domain.Category;
import com.cmsnesia.domain.repository.CategoryRepo;
import com.cmsnesia.model.CategoryDto;
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
@Service("categoryDeleteCommand")
public class DeleteCommand extends AbstractCommand<CategoryDto, Result<CategoryDto>> {

  private final CategoryAssembler categoryAssembler;
  private final CategoryRepo categoryRepo;

  @Override
  public Publisher<Result<CategoryDto>> execute(Session session, CategoryDto dto) {
    return categoryRepo
        .find(session, dto.getId(), dto.getLink())
        .defaultIfEmpty(Category.builder().build())
        .flatMap(
            category -> {
              if (StringUtils.isEmpty(category.getId())) {
                return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
              } else {
                category.setDeletedBy(session.getId());
                category.setDeletedAt(new Date());
                return categoryRepo
                    .save(category)
                    .map(saved -> categoryAssembler.fromEntity(saved))
                    .map(result -> Result.build(result, StatusCode.DELETE_SUCCESS));
              }
            });
  }
}
