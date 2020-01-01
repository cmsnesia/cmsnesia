package id.or.gri.service.impl;

import id.or.gri.assembler.PostAssembler;
import id.or.gri.domain.Post;
import id.or.gri.domain.model.Author;
import id.or.gri.model.AuthDto;
import id.or.gri.model.CategoryDto;
import id.or.gri.model.PostDto;
import id.or.gri.model.TagDto;
import id.or.gri.service.CategoryService;
import id.or.gri.service.PostService;
import id.or.gri.service.TagService;
import id.or.gri.service.repository.PostRepo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PostServiceImpl implements PostService {

    private final PostAssembler postAssembler;
    private final PostRepo postRepo;
    private final TagService tagService;
    private final CategoryService categoryService;

    public PostServiceImpl(PostAssembler postAssembler, PostRepo postRepo, TagService tagService, CategoryService categoryService) {
        this.postAssembler = postAssembler;
        this.postRepo = postRepo;
        this.tagService = tagService;
        this.categoryService = categoryService;
    }

    @Override
    public Mono<PostDto> add(AuthDto authDto, PostDto dto) {
        Post post = postAssembler.fromDto(dto);

        Author author = new Author();
        author.setName(authDto.getFullName());
        author.setModifiedAt(new Date());

        post.setId(UUID.randomUUID().toString());
        post.setAuthors(Arrays.asList(author).stream().collect(Collectors.toSet()));

        post.setViewCount(0L);
        post.setLikeCount(0L);
        post.setDislikeCount(0L);
        post.setShareCount(0L);

        post.setCreatedBy(authDto.getId());
        post.setCreatedAt(new Date());

        Boolean tagIsExist = tagService.exists(dto.getTags().stream().map(TagDto::getId).collect(Collectors.toSet())).block();
        Boolean categoryIsExist = categoryService.exists(dto.getCategories().stream().map(CategoryDto::getId).collect(Collectors.toSet())).block();
        if (tagIsExist != null && categoryIsExist != null && tagIsExist && categoryIsExist) {
            return postRepo.save(post).map(postAssembler::fromEntity);
        }
        return Mono.empty();
    }

    @Override
    public Mono<PostDto> edit(AuthDto authDto, PostDto dto) {
        return postRepo.findById(dto.getId())
                .flatMap((Function<Post, Mono<PostDto>>) post -> {
                    Post save = postAssembler.fromDto(dto);
                    save.audit(post);

                    if (!post.getAuthors().stream().map(Author::getName)
                            .collect(Collectors.toSet()).contains(authDto.getFullName())) {
                        Author author = new Author();
                        author.setName(authDto.getFullName());
                        author.setModifiedAt(new Date());
                        post.getAuthors().add(author);
                    }

                    save.setModifiedBy(authDto.getId());
                    save.setModifiedAt(new Date());

                    Boolean tagIsExist = tagService.exists(dto.getTags().stream().map(TagDto::getId).collect(Collectors.toSet())).block();
                    Boolean categoryIsExist = categoryService.exists(dto.getCategories().stream().map(CategoryDto::getId).collect(Collectors.toSet())).block();
                    if (tagIsExist != null && categoryIsExist != null && tagIsExist && categoryIsExist) {
                        return postRepo
                                .save(save)
                                .map(result -> postAssembler.fromEntity(result));
                    }
                    return Mono.empty();
                });
    }

    @Override
    public Mono<PostDto> delete(AuthDto authDto, PostDto dto) {
        return postRepo.findById(dto.getId())
                .flatMap((Function<Post, Mono<PostDto>>) post -> {
                    post.setDeletedBy(authDto.getId());
                    post.setDeletedAt(new Date());
                    return postRepo
                            .save(post)
                            .map(result -> postAssembler.fromEntity(result));
                });
    }

    @Override
    public Flux<PostDto> find(AuthDto authDto, PostDto dto, Pageable pageable) {
        return postRepo.find(authDto, dto, pageable)
                .map(post -> postAssembler.fromEntity(post));
    }
}
