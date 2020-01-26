package com.cmsnesia.web.controller;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.model.request.NameRequest;
import com.cmsnesia.model.request.PageRequest;
import com.cmsnesia.service.CategoryService;
import com.cmsnesia.web.util.ConstantKeys;
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
@RequestMapping(value = "category")
@Api(value = "Category API", tags = {"Category"})
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/find")
    @ApiOperation(
            value = "List category",
            response = AuthDto.class,
            notes = "Flux [CategoryDto]")
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
    public Flux<CategoryDto> find(@RequestBody CategoryDto categoryDto, PageRequest pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMapMany(session -> {
                    return categoryService.find(session, categoryDto, org.springframework.data.domain.PageRequest.of(pageable.getPage(), pageable.getSize()));
                });
    }

    @ApiOperation(
            value = "Add Category",
            response = AuthDto.class,
            notes = "Mono [CategoryDto]")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = ConstantKeys.AUTHORIZATION,
                    paramType = "header",
                    dataType = "string")
    })
    @PostMapping("/add")
    public Mono<CategoryDto> add(@RequestBody NameRequest nameRequest) {
        CategoryDto categoryDto = CategoryDto.builder()
                .name(nameRequest.getName())
                .build();
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMap(session -> {
                    return categoryService.add(session, categoryDto);
                });
    }

    @ApiOperation(
            value = "Edit category",
            response = AuthDto.class,
            notes = "Mono [CategoryDto]")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = ConstantKeys.AUTHORIZATION,
                    paramType = "header",
                    dataType = "string")
    })
    @PutMapping("/edit")
    public Mono<CategoryDto> edit(@RequestBody CategoryDto categoryDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMap(session -> {
                    return categoryService.edit(session, categoryDto);
                });
    }

    @ApiOperation(
            value = "Soft delete category",
            response = AuthDto.class,
            notes = "Mono [CategoryDto]")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = ConstantKeys.AUTHORIZATION,
                    paramType = "header",
                    dataType = "string")
    })
    @PutMapping("/delete")
    public Mono<CategoryDto> delete(@RequestBody IdRequest idRequest) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (AuthDto) authentication.getPrincipal())
                .flatMap(session -> {
                    CategoryDto dto = new CategoryDto();
                    dto.setId(idRequest.getId());
                    return categoryService.delete(session, dto);
                });
    }

}
