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
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CreateCommand extends AbstractCommand<CategoryDto, Result<CategoryDto>> {

  private final CategoryAssembler categoryAssembler;
  private final CategoryRepo categoryRepo;

  @Override
  public Publisher<Result<CategoryDto>> execute(Session session, CategoryDto dto) {
    Mono<Boolean> categoryIdExist = postIsExist(session, dto);
    return categoryIdExist.flatMap(
        exists -> {
          if (!exists) {
            Category category = categoryAssembler.fromDto(dto);
            category.setId(UUID.randomUUID().toString());
            category.setCreatedBy(session.getId());
            category.setCreatedAt(new Date());
            category.setApplications(Session.applications(session));
            return categoryRepo
                .save(category)
                .map(categoryAssembler::fromEntity)
                .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
          } else {
            return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
          }
        });
  }

  private Mono<Boolean> postIsExist(Session session, CategoryDto categoryDto) {
    return categoryRepo.exists(session, null, categoryDto.getName(), categoryDto.getLink());
  }
}
