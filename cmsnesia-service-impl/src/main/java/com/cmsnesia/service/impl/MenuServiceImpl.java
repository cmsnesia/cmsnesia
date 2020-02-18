package com.cmsnesia.service.impl;

import com.cmsnesia.assembler.MenuAssembler;
import com.cmsnesia.domain.Menu;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.MenuDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.MenuService;
import com.cmsnesia.service.repository.CategoryRepo;
import com.cmsnesia.service.repository.MenuRepo;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MenuServiceImpl implements MenuService {

  private final MenuAssembler menuAssembler;
  private final MenuRepo menuRepo;
  private final CategoryRepo categoryRepo;

  @Override
  public Mono<Result<MenuDto>> add(AuthDto session, MenuDto dto) {

    if (Collections.isEmpty(dto.getCategories())) {
      return Mono.empty();
    }

    Set<String> categoryIds =
        dto.getCategories().stream().map(CategoryDto::getId).collect(Collectors.toSet());

    return categoryRepo
        .exists(session, categoryIds)
        .flatMap(
            exists -> {
              if (exists) {
                Menu menu = menuAssembler.fromDto(dto);
                menu.setId(UUID.randomUUID().toString());
                menu.setCreatedAt(new Date());
                menu.setCreatedBy(session.getId());
                return menuRepo
                    .save(menu)
                    .map(menuAssembler::fromEntity)
                    .map(menuDto -> Result.build(menuDto, StatusCode.SAVE_SUCCESS));
              } else {
                return Mono.empty();
              }
            });
  }

  @Override
  public Mono<Result<MenuDto>> edit(AuthDto session, MenuDto dto) {

    if (Collections.isEmpty(dto.getCategories())) {
      return Mono.empty();
    }
    Set<String> categoryIds =
        dto.getCategories().stream().map(CategoryDto::getId).collect(Collectors.toSet());

    return categoryRepo
        .exists(session, categoryIds)
        .flatMap(
            exists -> {
              if (exists) {
                return menuRepo
                    .findById(dto.getId())
                    .flatMap(
                        menu -> {
                          Menu save = menuAssembler.fromDto(dto);
                          save.audit(menu);
                          save.setModifiedAt(new Date());
                          save.setModifiedBy(session.getId());
                          return menuRepo.save(save);
                        })
                    .map(menuAssembler::fromEntity)
                    .map(menuDto -> Result.build(menuDto, StatusCode.SAVE_SUCCESS));
              } else {
                return Mono.empty();
              }
            });
  }

  @Override
  public Mono<Result<MenuDto>> delete(AuthDto session, MenuDto dto) {
    return menuRepo
        .findById(dto.getId())
        .flatMap(
            menu -> {
              Menu save = menuAssembler.fromDto(dto);
              save.audit(menu);
              save.setDeletedAt(new Date());
              save.setDeletedBy(session.getId());
              return menuRepo.save(save);
            })
        .map(menuAssembler::fromEntity)
        .map(menuDto -> Result.build(menuDto, StatusCode.DELETE_SUCCESS));
  }

  @Override
  public Mono<Page<MenuDto>> find(AuthDto session, MenuDto dto, Pageable pageable) {
    return menuRepo
        .countFind(session, dto)
        .flatMap(
            count -> {
              Mono<List<MenuDto>> mono =
                  menuRepo
                      .find(session, dto, pageable)
                      .map(menu -> menuAssembler.fromEntity(menu))
                      .collectList();
              return mono.map(menuDtos -> new PageImpl<>(menuDtos, pageable, count));
            });
  }

  @Override
  public Mono<Result<MenuDto>> find(AuthDto session, IdRequest idRequest) {
    return menuRepo
        .find(session, idRequest)
        .map(menuAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }

  @Override
  public Mono<Result<MenuDto>> findById(AuthDto session, IdRequest id) {
    return menuRepo
        .findById(id.getId())
        .map(menuAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }
}
