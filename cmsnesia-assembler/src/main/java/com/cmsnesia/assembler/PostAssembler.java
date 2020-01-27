package com.cmsnesia.assembler;

import com.cmsnesia.domain.Post;
import com.cmsnesia.domain.PostDraft;
import com.cmsnesia.domain.model.Category;
import com.cmsnesia.domain.model.Tag;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.PostDto;
import com.cmsnesia.model.TagDto;
import com.cmsnesia.model.util.DateTimeUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PostAssembler implements Assembler<Post, PostDto> {

    private final AuthorAssembler authorAssembler;
    private final MediaAssembler mediaAssembler;

    public PostAssembler(AuthorAssembler authorAssembler, MediaAssembler mediaAssembler) {
        this.authorAssembler = authorAssembler;
        this.mediaAssembler = mediaAssembler;
    }

    @Nonnull
    @Override
    public Post fromDto(@Nonnull PostDto dto) {
        Set<Tag> tags = new HashSet<>();
        if (dto.getTags() != null) {
            tags.addAll(dto.getTags().stream().map(tagDto -> Tag.builder()
                    .createdAt(new Date())
                    .name(tagDto.getName())
                    .build()).collect(Collectors.toSet()));
        }
        return Post.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .authors(dto.getAuthors() == null ? new HashSet<>() : authorAssembler.fromDto(dto.getAuthors()))
                .medias(dto.getMedias() == null ? new HashSet<>() : mediaAssembler.fromDto(dto.getMedias()))
                .tags(tags)
                .categories(fromDto(dto.getCategories()))
                .viewCount(dto.getViewCount())
                .likeCount(dto.getLikeCount())
                .dislikeCount(dto.getDislikeCount())
                .shareCount(dto.getShareCount())
                .build();
    }

    @Nonnull
    @Override
    public Set<Post> fromDto(@Nonnull Collection<PostDto> dtos) {
        return dtos.stream().map(this::fromDto).collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public PostDto fromEntity(@Nonnull Post entity) {
        return PostDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .authors(entity.getAuthors() == null ? new HashSet<>() : authorAssembler.fromEntity(entity.getAuthors()))
                .medias(entity.getMedias() == null ? new HashSet<>() : mediaAssembler.fromEntity(entity.getMedias()))
                .viewCount(entity.getViewCount())
                .likeCount(entity.getLikeCount())
                .dislikeCount(entity.getDislikeCount())
                .shareCount(entity.getShareCount())
                .build();
    }

    @Nonnull
    @Override
    public Set<PostDto> fromEntity(@Nonnull Collection<Post> entities) {
        return entities.stream().map(this::fromEntity).collect(Collectors.toSet());
    }

    public PostDto fromDraft(PostDraft postDraft) {
        Set<TagDto> tagDtos = new HashSet<>();
        if (postDraft.getTags() != null) {
            tagDtos.addAll(postDraft.getTags().stream().map(tag -> TagDto.builder()
                    .createdAt(DateTimeUtils.toString(tag.getCreatedAt()))
                    .createdBy(tag.getCreatedBy())
                    .name(tag.getName())
                    .build()).collect(Collectors.toSet()));
        }
        return PostDto.builder()
                .id(postDraft.getId())
                .title(postDraft.getTitle())
                .content(postDraft.getContent())
                .medias(mediaAssembler.fromEntity(postDraft.getMedias()))
                .tags(tagDtos)
                .categories(fromModel(postDraft.getCategories()))
                .build();
    }

    public PostDraft fromPostDto(PostDto postDto) {
        Set<Tag> tags = new HashSet<>();
        if (postDto.getTags() != null) {
            tags.addAll(postDto.getTags().stream().map(tagDto -> Tag.builder()
                    .createdAt(new Date())
                    .name(tagDto.getName())
                    .build()).collect(Collectors.toSet()));
        }
        return PostDraft.builder()
                .id(postDto.getId())
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .medias(mediaAssembler.fromDto(postDto.getMedias()))
                .tags(tags)
                .categories(fromDto(postDto.getCategories()))
                .build();
    }

    private Set<Category> fromDto(Set<CategoryDto> categoryDtos) {
        return categoryDtos.stream().map(categoryDto -> {
            return Category.builder()
                    .id(categoryDto.getId())
                    .name(categoryDto.getName())
                    .build();
        }).collect(Collectors.toSet());
    }

    private Set<CategoryDto> fromModel(Set<Category> categories) {
        return categories.stream().map(category -> {
            return CategoryDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .build();
        }).collect(Collectors.toSet());
    }

//    public PostDraft toDraft(PostDto postDto) {
//        PostDraft postDraft = PostDraft.builder()
//                .postId(postDto.getId())
//                .title(postDto.getTitle())
//                .content(postDto.getContent())
//                .medias(mediaAssembler.fromDto(postDto.getMedias()))
//                .tags(tagAssembler.fromDto(postDto.getTags()))
//                .categories(categoryAssembler.fromDto(postDto.getCategories()))
//                .build();
//        return postDraft;
//    }

}
