package id.or.gri.web.controller;

import id.or.gri.model.AuthDto;
import id.or.gri.model.CategoryDto;
import id.or.gri.model.TagDto;
import id.or.gri.model.request.IdRequest;
import id.or.gri.model.request.NameRequest;
import id.or.gri.model.request.PageRequest;
import id.or.gri.service.CategoryService;
import id.or.gri.service.TagService;
import id.or.gri.web.util.ConstantKeys;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "tag")
@Api(value = "Tag API", tags = {"Tag"})
@Slf4j
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("/find")
    @ApiOperation(
            value = "List tag",
            response = AuthDto.class,
            notes = "Flux [TagDto]")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = ConstantKeys.AUTHORIZATION,
                    paramType = "header",
                    dataType = "string"),
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
    public Flux<TagDto> find(@RequestBody TagDto tagDto, PageRequest pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMapMany(session -> {
                    return tagService.find(session, tagDto, org.springframework.data.domain.PageRequest.of(pageable.getPage(), pageable.getSize()));
                });
    }

    @ApiOperation(
            value = "Add tag",
            response = AuthDto.class,
            notes = "Mono [TagDto]")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = ConstantKeys.AUTHORIZATION,
                    paramType = "header",
                    dataType = "string")
    })
    @PostMapping("/add")
    public Mono<TagDto> add(@RequestBody NameRequest nameRequest) {
        TagDto tagDto = TagDto.builder()
                .name(nameRequest.getName())
                .build();
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMap(session -> {
                    return tagService.add(session, tagDto);
                });
    }

    @ApiOperation(
            value = "Edit tag",
            response = AuthDto.class,
            notes = "Mono [TagDto]")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = ConstantKeys.AUTHORIZATION,
                    paramType = "header",
                    dataType = "string")
    })
    @PutMapping("/edit")
    public Mono<TagDto> edit(@RequestBody TagDto tagDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMap(session -> {
                    return tagService.edit(session, tagDto);
                });
    }

    @ApiOperation(
            value = "Soft delete tag",
            response = AuthDto.class,
            notes = "Mono [TagDto]")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = ConstantKeys.AUTHORIZATION,
                    paramType = "header",
                    dataType = "string")
    })
    @PutMapping("/delete")
    public Mono<TagDto> delete(@RequestBody IdRequest idRequest) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMap(session -> {
                    TagDto dto = new TagDto();
                    dto.setId(idRequest.getId());
                    return tagService.delete(session, dto);
                });
    }

}
