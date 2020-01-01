package id.or.gri.service.impl;

import id.or.gri.assembler.CategoryAssembler;
import id.or.gri.domain.Category;
import id.or.gri.model.AuthDto;
import id.or.gri.model.CategoryDto;
import id.or.gri.service.CategoryService;
import id.or.gri.service.repository.CategoryRepo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryAssembler categoryAssembler;
    private final CategoryRepo categoryRepo;

    public CategoryServiceImpl(CategoryAssembler categoryAssembler, CategoryRepo categoryRepo) {
        this.categoryAssembler = categoryAssembler;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public Mono<CategoryDto> add(AuthDto authDto, CategoryDto dto) {
        Category category = categoryAssembler.fromDto(dto);
        category.setId(UUID.randomUUID().toString());
        category.setCreatedBy(authDto.getId());
        category.setCreatedAt(new Date());
        return categoryRepo.save(category).map(categoryAssembler::fromEntity);
    }

    @Override
    public Mono<CategoryDto> edit(AuthDto authDto, CategoryDto dto) {
        return categoryRepo.findById(dto.getId())
                .flatMap((Function<Category, Mono<CategoryDto>>) category -> {
                    Category save = categoryAssembler.fromDto(dto);
                    save.audit(category);
                    save.setModifiedBy(authDto.getId());
                    save.setModifiedAt(new Date());
                    return categoryRepo
                            .save(save)
                            .map(result -> categoryAssembler.fromEntity(result));
                });
    }

    @Override
    public Mono<CategoryDto> delete(AuthDto authDto, CategoryDto dto) {
        return categoryRepo.findById(dto.getId())
                .flatMap((Function<Category, Mono<CategoryDto>>) category -> {
                    category.setDeletedBy(authDto.getId());
                    category.setDeletedAt(new Date());
                    return categoryRepo
                            .save(category)
                            .map(result -> categoryAssembler.fromEntity(result));
                });
    }

    @Override
    public Flux<CategoryDto> find(AuthDto authDto, CategoryDto dto, Pageable pageable) {
        return categoryRepo.find(authDto, dto, pageable)
                .map(category -> categoryAssembler.fromEntity(category));
    }

    @Override
    public Mono<Boolean> exists(Set<String> ids) {
        return categoryRepo.exists(ids);
    }
}
