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
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service("categoryEditCommand")
public class EditCommand extends AbstractCommand<CategoryDto, Result<CategoryDto>> {

  private final CategoryAssembler categoryAssembler;
  private final CategoryRepo categoryRepo;
  private final PostRepo postRepo;

  @Transactional
  @Override
  public Publisher<Result<CategoryDto>> execute(Session session, CategoryDto dto) {
    Mono<Boolean> categoryIdExist = postIsExist(session, dto);
    return categoryIdExist.flatMap(
        exists -> {
          if (!exists) {
            return categoryRepo
                .find(session, dto.getId(), dto.getLink())
                .flatMap(
                    category -> {
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
                    });
          } else {
            return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
          }
        });
  }

  private Mono<Boolean> postIsExist(Session session, CategoryDto categoryDto) {
    return categoryRepo.exists(session, new HashSet<>(Arrays.asList(categoryDto.getId())));
  }

  public Mono<UpdateResult> modifyPostCategory(Session session, CategoryDto categoryDto) {
    return postRepo.findAndModifyCategory(session, categoryDto);
  }
}
