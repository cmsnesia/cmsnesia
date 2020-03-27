package com.cmsnesia.service.command.category;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.CategoryAssembler;
import com.cmsnesia.domain.repository.CategoryRepo;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.service.command.AbstractCommand;
import com.cmsnesia.service.command.Command;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service("categoryFindAllCommand")
public class FindAllCommand
    extends AbstractCommand<Command.PageableRequest<CategoryDto>, Page<CategoryDto>> {

  private final CategoryAssembler categoryAssembler;
  private final CategoryRepo categoryRepo;

  @Override
  public Publisher<Page<CategoryDto>> execute(
      Session session, PageableRequest<CategoryDto> request) {
    CategoryDto dto = request.getData();
    Pageable pageable = request.getPageable();
    Mono<Long> count = categoryRepo.countFind(session, dto);
    Mono<List<CategoryDto>> posts =
        categoryRepo.find(session, dto, pageable).map(categoryAssembler::fromEntity).collectList();
    return Mono.zip(count, posts)
        .map(tuple -> new PageImpl<>(tuple.getT2(), pageable, tuple.getT1()));
  }
}
