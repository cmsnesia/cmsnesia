package com.cmsnesia.service.command.category;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.CategoryAssembler;
import com.cmsnesia.domain.Category;
import com.cmsnesia.domain.repository.CategoryRepo;
import com.cmsnesia.domain.repository.PostRepo;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.service.command.AbstractCommand;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Date;

@RequiredArgsConstructor
@Service("categoryEditCommand")
public class EditCommand extends AbstractCommand<CategoryDto, Result<CategoryDto>> {

  private final CategoryAssembler categoryAssembler;
  private final CategoryRepo categoryRepo;
  private final PostRepo postRepo;

  @Transactional
  @Override
  public Publisher<Result<CategoryDto>> execute(Session session, CategoryDto dto) {
    Mono<Boolean> categoryIdExist = categoryIsExist(session, dto);
    return categoryIdExist.flatMap(
        exists -> {
          if (!exists) {
            return categoryRepo
                .find(session, dto.getId(), dto.getLink())
                .defaultIfEmpty(Category.builder().build())
                .flatMap(
                    category -> {
                      if (StringUtils.isEmpty(category.getId())) {
                        return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
                      } else {
                        Category save = categoryAssembler.fromDto(dto);
                        save.audit(category);
                        save.setModifiedBy(session.getId());
                        save.setModifiedAt(new Date());
                        Mono<Category> saved = categoryRepo.save(save);
                        Mono<UpdateResult> updated = modifyPostCategory(session, dto);
                        return Mono.zip(saved, updated)
                            .map(
                                tuple -> {
                                  if (tuple.getT1() != null && tuple.getT2() != null) {
                                    return Result.build(
                                        categoryAssembler.fromEntity(tuple.getT1()),
                                        StatusCode.SAVE_SUCCESS);
                                  } else {
                                    return Result.build(StatusCode.SAVE_FAILED);
                                  }
                                });
                      }
                    });
          } else {
            return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
          }
        });
  }

  private Mono<Boolean> categoryIsExist(Session session, CategoryDto categoryDto) {
    return categoryRepo.exists(session, null, categoryDto.getName(), categoryDto.getLink());
  }

  public Mono<UpdateResult> modifyPostCategory(Session session, CategoryDto categoryDto) {
    return postRepo.findAndModifyCategory(session, categoryDto);
  }
}
