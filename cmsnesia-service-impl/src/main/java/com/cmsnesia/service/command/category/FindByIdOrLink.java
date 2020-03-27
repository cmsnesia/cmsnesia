package com.cmsnesia.service.command.category;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.CategoryAssembler;
import com.cmsnesia.domain.repository.CategoryRepo;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.service.command.AbstractCommand;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FindByIdOrLink extends AbstractCommand<CategoryDto, Result<CategoryDto>> {

  private final CategoryAssembler categoryAssembler;
  private final CategoryRepo categoryRepo;

  @Override
  public Publisher<Result<CategoryDto>> execute(Session session, CategoryDto request) {
    return categoryRepo
        .find(session, request.getId(), request.getLink())
        .defaultIfEmpty(null)
        .map(
            category -> {
              if (category == null) {
                return Result.build(StatusCode.DATA_NOT_FOUND);
              } else {
                return Result.build(categoryAssembler.fromEntity(category), StatusCode.DATA_FOUND);
              }
            });
  }
}
