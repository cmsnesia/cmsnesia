package com.cmsnesia.service.impl;

import com.cmsnesia.assembler.TagAssembler;
import cmsnesia.domain.Tag;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.TagDto;
import com.cmsnesia.service.repository.TagRepo;
import com.cmsnesia.service.TagService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Component
@Deprecated
public class TagServiceImpl implements TagService {

    private final TagAssembler tagAssembler;
    private final TagRepo tagRepo;

    public TagServiceImpl(TagAssembler tagAssembler, TagRepo tagRepo) {
        this.tagAssembler = tagAssembler;
        this.tagRepo = tagRepo;
    }

    @Override
    public Mono<TagDto> add(AuthDto authDto, TagDto dto) {
        Tag tag = tagAssembler.fromDto(dto);
        tag.setId(UUID.randomUUID().toString());
        tag.setCreatedBy(authDto.getId());
        tag.setCreatedAt(new Date());
        return tagRepo.save(tag).map(tagAssembler::fromEntity);
    }

    @Override
    public Mono<TagDto> edit(AuthDto authDto, TagDto dto) {
        return tagRepo.findById(dto.getId())
                .flatMap((Function<Tag, Mono<TagDto>>) tag -> {
                    Tag save = tagAssembler.fromDto(dto);
                    save.audit(tag);
                    save.setModifiedBy(authDto.getId());
                    save.setModifiedAt(new Date());
                    return tagRepo
                            .save(save)
                            .map(result -> tagAssembler.fromEntity(result));
                });
    }

    @Override
    public Mono<TagDto> delete(AuthDto authDto, TagDto dto) {
        return tagRepo.findById(dto.getId())
                .flatMap((Function<Tag, Mono<TagDto>>) tag -> {
                    tag.setDeletedBy(authDto.getId());
                    tag.setDeletedAt(new Date());
                    return tagRepo
                            .save(tag)
                            .map(result -> tagAssembler.fromEntity(result));
                });
    }

    @Override
    public Flux<TagDto> find(AuthDto authDto, TagDto dto, Pageable pageable) {
        return tagRepo.find(authDto, dto, pageable)
                .map(tag -> tagAssembler.fromEntity(tag));
    }

    @Override
    public Mono<Boolean> exists(Set<String> ids) {
        return tagRepo.exists(ids);
    }
}
