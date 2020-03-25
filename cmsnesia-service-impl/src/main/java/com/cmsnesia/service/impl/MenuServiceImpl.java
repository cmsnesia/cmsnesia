package com.cmsnesia.service.impl;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.MenuAssembler;
import com.cmsnesia.domain.Menu;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.MenuDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.MenuService;
import com.cmsnesia.domain.repository.CategoryRepo;
import com.cmsnesia.domain.repository.MenuRepo;
import io.jsonwebtoken.lang.Collections;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class MenuServiceImpl implements MenuService {

  private final MenuAssembler menuAssembler;
  private final MenuRepo menuRepo;
  private final CategoryRepo categoryRepo;

  @Override
  public Mono<Result<MenuDto>> add(Session session, MenuDto dto) {
    return menuRepo
        .exists(session, null, dto.getName())
        .flatMap(
            dataExist -> {
              if (!dataExist) {
                if (Collections.isEmpty(dto.getCategories())) {
                  return Mono.empty();
                }

                Set<String> categoryIds =
                    dto.getCategories().stream()
                        .map(CategoryDto::getId)
                        .collect(Collectors.toSet());

                return categoryRepo
                    .exists(session, categoryIds)
                    .flatMap(
                        exists -> {
                          if (exists) {
                            Menu menu = menuAssembler.fromDto(dto);
                            menu.setId(UUID.randomUUID().toString());
                            menu.setCreatedAt(new Date());
                            menu.setCreatedBy(session.getId());
                            menu.setApplications(Session.applications(session));
                            return menuRepo
                                .save(menu)
                                .map(menuAssembler::fromEntity)
                                .map(menuDto -> Result.build(menuDto, StatusCode.SAVE_SUCCESS));
                          } else {
                            return Mono.empty();
                          }
                        });
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }

  @Override
  public Mono<Result<MenuDto>> edit(Session session, MenuDto dto) {
    return menuRepo
        .exists(session, dto.getId(), dto.getName())
        .flatMap(
            dataExist -> {
              if (!dataExist) {
                if (Collections.isEmpty(dto.getCategories())) {
                  return Mono.empty();
                }
                Set<String> categoryIds =
                    dto.getCategories().stream()
                        .map(CategoryDto::getId)
                        .collect(Collectors.toSet());

                return categoryRepo
                    .exists(session, categoryIds)
                    .flatMap(
                        exists -> {
                          if (exists) {
                            return menuRepo
                                .find(session, IdRequest.builder().id(dto.getId()).build())
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
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }

  @Override
  public Mono<Result<MenuDto>> delete(Session session, MenuDto dto) {
    return menuRepo
        .exists(session, new HashSet<>(Arrays.asList(dto.getId())))
        .flatMap(
            exists -> {
              if (exists) {
                return menuRepo
                    .find(session, IdRequest.builder().id(dto.getId()).build())
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
              } else {
                return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
              }
            });
  }

  @Override
  public Mono<Page<MenuDto>> find(Session session, MenuDto dto, Pageable pageable) {
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
  public Mono<Result<MenuDto>> find(Session session, IdRequest idRequest) {
    return menuRepo
        .find(session, idRequest)
        .map(menuAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }
}
