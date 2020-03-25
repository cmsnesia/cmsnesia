package com.cmsnesia.service.impl;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.assembler.EventAssembler;
import com.cmsnesia.domain.Event;
import com.cmsnesia.model.EventDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.EventService;
import com.cmsnesia.domain.repository.EventRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

@Service
public class EventServiceImpl implements EventService {

  private final EventAssembler eventAssembler;
  private final EventRepo eventRepo;

  public EventServiceImpl(EventAssembler eventAssembler, EventRepo eventRepo) {
    this.eventAssembler = eventAssembler;
    this.eventRepo = eventRepo;
  }

  @Override
  public Mono<Result<EventDto>> add(Session session, EventDto dto) {
    return eventRepo
        .exists(session, new HashSet<>(Arrays.asList(dto.getId())))
        .flatMap(
            exists -> {
              if (!exists) {
                Event event = eventAssembler.fromDto(dto);
                event.setCreatedBy(session.getId());
                event.setCreatedAt(new Date());
                return eventRepo
                    .save(event)
                    .map(eventAssembler::fromEntity)
                    .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }

  @Override
  public Mono<Result<EventDto>> edit(Session session, EventDto dto) {
    return eventRepo
        .exists(session, dto.getId(), dto.getName())
        .flatMap(
            exists -> {
              if (!exists) {
                return eventRepo
                    .find(session, IdRequest.builder().id(dto.getId()).build())
                    .flatMap(
                        event -> {
                          Event save = eventAssembler.fromDto(dto);
                          save.audit(event);
                          save.setModifiedBy(session.getId());
                          save.setModifiedAt(new Date());
                          return eventRepo
                              .save(save)
                              .map(eventAssembler::fromEntity)
                              .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
                        });
              } else {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
            });
  }

  @Override
  public Mono<Result<EventDto>> delete(Session session, EventDto dto) {
    return eventRepo
        .exists(session, new HashSet<>(Arrays.asList(dto.getId())))
        .flatMap(
            exists -> {
              if (exists) {
                return eventRepo
                    .find(session, IdRequest.builder().id(dto.getId()).build())
                    .flatMap(
                        event -> {
                          event.setDeletedBy(session.getId());
                          event.setDeletedAt(new Date());
                          return eventRepo
                              .save(event)
                              .map(eventAssembler::fromEntity)
                              .map(result -> Result.build(StatusCode.DELETE_SUCCESS));
                        });
              } else {
                return Mono.just(Result.build(StatusCode.DATA_NOT_FOUND));
              }
            });
  }

  @Override
  public Mono<Page<EventDto>> find(Session session, EventDto dto, Pageable pageable) {
    return eventRepo
        .countFind(session, dto)
        .flatMap(
            count -> {
              if (count == null || count == 0L) {
                return Mono.just(new PageImpl<>(Collections.emptyList(), pageable, 0L));
              } else {
                return eventRepo
                    .find(session, dto, pageable)
                    .map(eventAssembler::fromEntity)
                    .collectList()
                    .map(eventDtos -> new PageImpl<>(eventDtos, pageable, count));
              }
            });
  }

  @Override
  public Mono<Result<EventDto>> find(Session session, IdRequest idRequest) {
    return eventRepo
        .find(session, idRequest)
        .map(eventAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }
}
