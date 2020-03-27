package com.cmsnesia.service.command.category;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.repository.CategoryRepo;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.command.AbstractCommand;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("categoryMustExistCommand")
public class MustExistCommand extends AbstractCommand<Set<IdRequest>, Result<Boolean>> {

  private final CategoryRepo categoryRepo;

  @Override
  public Publisher<Result<Boolean>> execute(Session session, Set<IdRequest> request) {
    return categoryRepo
        .exists(session, request.stream().map(IdRequest::getId).collect(Collectors.toSet()))
        .map(
            result ->
                result
                    ? Result.build(true, StatusCode.DATA_FOUND)
                    : Result.build(false, StatusCode.DATA_NOT_FOUND));
  }
}
