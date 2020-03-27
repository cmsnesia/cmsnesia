package com.cmsnesia.service.command.category;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.CategoryAssembler;
import com.cmsnesia.domain.repository.CategoryRepo;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.command.AbstractCommand;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FindByIdsCommand extends AbstractCommand<Set<IdRequest>, Set<CategoryDto>> {

  private final CategoryAssembler categoryAssembler;
  private final CategoryRepo categoryRepo;

  @Override
  public Publisher<Set<CategoryDto>> execute(Session session, Set<IdRequest> request) {
    return categoryRepo
        .find(session, request.stream().map(IdRequest::getId).collect(Collectors.toSet()))
        .collectList()
        .defaultIfEmpty(new ArrayList<>())
        .map(categories -> new HashSet<>(categoryAssembler.fromEntity(categories)));
  }
}
