package id.or.gri.service.impl;

import id.or.gri.assembler.PostAssembler;
import id.or.gri.domain.Post;
import id.or.gri.domain.PostDraft;
import id.or.gri.domain.model.Author;
import id.or.gri.domain.model.Tag;
import id.or.gri.domain.model.enums.PostStatus;
import id.or.gri.model.AuthDto;
import id.or.gri.model.CategoryDto;
import id.or.gri.model.PostDto;
import id.or.gri.model.request.IdRequest;
import id.or.gri.service.CategoryService;
import id.or.gri.service.PostService;
import id.or.gri.service.repository.PostDraftRepo;
import id.or.gri.service.repository.PostRepo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PostServiceImpl implements PostService {

    private final PostAssembler postAssembler;
    private final PostRepo postRepo;
    private final PostDraftRepo postDraftRepo;
    private final CategoryService categoryService;

    public PostServiceImpl(PostAssembler postAssembler, PostRepo postRepo, PostDraftRepo postDraftRepo, CategoryService categoryService) {
        this.postAssembler = postAssembler;
        this.postRepo = postRepo;
        this.postDraftRepo = postDraftRepo;
        this.categoryService = categoryService;
    }

    @Override
    public Mono<PostDto> add(AuthDto authDto, PostDto dto) {
        PostDraft post = postAssembler.fromPostDto(dto);

        post.setId(UUID.randomUUID().toString());

        post.setCreatedBy(authDto.getId());
        post.setCreatedAt(new Date());
        post.setStatus(Arrays.asList(PostStatus.UNPUBLISHED).stream().collect(Collectors.toSet()));

        post.getTags().forEach(tag -> tag.setCreatedBy(authDto.getId()));

        return categoryService.exists(dto.getCategories().stream().map(CategoryDto::getId).collect(Collectors.toSet()))
                .flatMap(categotyIsExist -> {
                    if (categotyIsExist != null && categotyIsExist) {
                        post.getTags().forEach(tag -> tag.setCreatedBy(authDto.getId()));
                        return postDraftRepo.save(post).map(postAssembler::fromDraft);
                    } else {
                        return Mono.empty();
                    }
                });
    }

    @Override
    public Mono<PostDto> edit(AuthDto authDto, PostDto dto) {
        return postDraftRepo.findById(dto.getId())
                .flatMap((Function<PostDraft, Mono<PostDto>>) post -> {
                    PostDraft save = postAssembler.fromPostDto(dto);

                    post.setStatus(Arrays.asList(PostStatus.UNPUBLISHED).stream().collect(Collectors.toSet()));

                    save.audit(post);

                    save.setModifiedBy(authDto.getId());
                    save.setModifiedAt(new Date());

                    Set<Tag> tags = new HashSet<>();
                    save.getTags().forEach(tag -> {
                        post.getTags().forEach(existing -> {
                            if (!(tag.getName().equals(existing.getName())
                                    && tag.getCreatedBy().equals(existing.getCreatedBy()))) {
                                tag.setCreatedBy(authDto.getId());
                                tags.add(tag);
                            }
                        });
                    });
                    save.setTags(tags);

                    return categoryService.exists(dto.getCategories().stream().map(CategoryDto::getId).collect(Collectors.toSet()))
                            .flatMap(categotyIsExist -> {
                                if (categotyIsExist != null && categotyIsExist) {
                                    return postDraftRepo.save(save).map(result -> postAssembler.fromDraft(result));
                                } else {
                                    return Mono.empty();
                                }
                            });
                });
    }

    @Override
    public Mono<PostDto> delete(AuthDto authDto, PostDto dto) {
        return postDraftRepo.findById(dto.getId())
                .flatMap((Function<PostDraft, Mono<PostDto>>) post -> {
                    post.setDeletedBy(authDto.getId());
                    post.setDeletedAt(new Date());
                    post.setStatus(Arrays.asList(PostStatus.UNPUBLISHED).stream().collect(Collectors.toSet()));
                    return postDraftRepo
                            .save(post)
                            .map(result -> postAssembler.fromDraft(result));
                });
    }

    @Override
    public Flux<PostDto> find(AuthDto authDto, PostDto dto, Pageable pageable) {
        return postRepo.find(authDto, dto, pageable)
                .map(post -> postAssembler.fromEntity(post));
    }

    @Override
    public Flux<PostDto> findDraft(AuthDto authDto, PostDto dto, Pageable pageable) {
        return postDraftRepo.find(authDto, dto, pageable)
                .map(postDraft -> postAssembler.fromDraft(postDraft));
    }

    @Override
    public Mono<PostDto> publish(AuthDto session, IdRequest id) {
        return postDraftRepo.findById(id.getId())
                .flatMap(postDraft -> {
                    Post newPost = postAssembler.fromDto(postAssembler.fromDraft(postDraft));
                    newPost.setCreatedAt(new Date());
                    newPost.setCreatedBy(session.getId());
                    return postRepo.findById(id.getId())
                            .defaultIfEmpty(newPost)
                            .flatMap(post -> {
                                Set<Author> authors = post.getAuthors() == null ? new HashSet<>() : post.getAuthors();
                                if (authors.stream().map(Author::getName).noneMatch(name -> name.equals(session.getFullName()))) {
                                    Author author = new Author();
                                    author.setName(session.getFullName());
                                    author.setModifiedAt(new Date());
                                    authors.add(author);
                                }
                                Post exitingPost = postAssembler.fromDto(postAssembler.fromDraft(postDraft));
                                exitingPost.setAuthors(authors);
                                exitingPost.audit(post);
                                exitingPost.setModifiedAt(new Date());
                                exitingPost.setModifiedBy(session.getId());
                                return postRepo.save(exitingPost)
                                        .flatMap(saved -> {
                                            postDraft.setStatus(Arrays.asList(PostStatus.PUBLISHED).stream().collect(Collectors.toSet()));
                                            return postDraftRepo.save(postDraft)
                                                    .map(result -> {
                                                        return postAssembler.fromEntity(saved);
                                                    });
                                        });
                            });
                });
    }
}
