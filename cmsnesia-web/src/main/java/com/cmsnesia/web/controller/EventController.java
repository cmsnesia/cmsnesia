package com.cmsnesia.web.controller;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.domain.Event;
import com.cmsnesia.model.EventDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.QueryPageRequest;
import com.cmsnesia.service.EventService;
import com.cmsnesia.web.util.ConstantKeys;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "event")
@Api(
    value = "Event API",
    tags = {"Event"})
@Slf4j
@RequiredArgsConstructor
public class EventController {

  private final EventService eventService;

  @ApiOperation(value = "Get event by ID", response = EventDto.class, notes = "Result<EventDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string")
  })
  @GetMapping("/findById")
  public Mono<Result<EventDto>> findById(@RequestParam("id") String id) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return eventService.find(session, IdRequest.builder().id(id).build());
            });
  }

  @PostMapping("/find")
  @ApiOperation(value = "List event", response = EventDto.class, notes = "Page<EventDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string"),
    @ApiImplicitParam(
        name = ConstantKeys.PAGE,
        defaultValue = "0",
        paramType = "query",
        dataType = "integer"),
    @ApiImplicitParam(
        name = ConstantKeys.SIZE,
        defaultValue = "10",
        paramType = "query",
        dataType = "integer")
  })
  public Mono<Page<EventDto>> find(
      @RequestBody EventDto eventDto,
      @PageableDefault(direction = Sort.Direction.DESC) QueryPageRequest pageable) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return eventService.find(
                  session, eventDto, PageRequest.of(pageable.getPage(), pageable.getSize()));
            });
  }

  @ApiOperation(value = "Add event", response = Event.class, notes = "Result<EventDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string")
  })
  @PostMapping("/add")
  public Mono<Result<EventDto>> add(@RequestBody EventDto eventDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return eventService.add(session, eventDto);
            });
  }

  @ApiOperation(value = "Edit event", response = EventDto.class, notes = "Result<EventDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string")
  })
  @PutMapping("/edit")
  public Mono<Result<EventDto>> edit(@RequestBody EventDto eventDto) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              return eventService.edit(session, eventDto);
            });
  }

  @ApiOperation(value = "Soft delete event", response = EventDto.class, notes = "Result<EventDto>")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.X_USER_DATA, paramType = "header", dataType = "string")
  })
  @PutMapping("/delete")
  public Mono<Result<EventDto>> delete(@RequestBody IdRequest idRequest) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> (Session) authentication.getPrincipal())
        .flatMap(
            session -> {
              EventDto dto = new EventDto();
              dto.setId(idRequest.getId());
              return eventService.delete(session, dto);
            });
  }
}
