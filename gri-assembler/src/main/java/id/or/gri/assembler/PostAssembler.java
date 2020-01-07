package id.or.gri.assembler;

import id.or.gri.domain.Post;
import id.or.gri.domain.PostDraft;
import id.or.gri.model.PostDto;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PostAssembler implements Assembler<Post, PostDto> {

    private final AuthorAssembler authorAssembler;
    private final MediaAssembler mediaAssembler;
    private final TagAssembler tagAssembler;
    private final CategoryAssembler categoryAssembler;

    public PostAssembler(AuthorAssembler authorAssembler, MediaAssembler mediaAssembler,
                         TagAssembler tagAssembler, CategoryAssembler categoryAssembler) {
        this.authorAssembler = authorAssembler;
        this.mediaAssembler = mediaAssembler;
        this.tagAssembler = tagAssembler;
        this.categoryAssembler = categoryAssembler;
    }

    @Nonnull
    @Override
    public Post fromDto(@Nonnull PostDto dto) {
        return Post.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .authors(dto.getAuthors() == null ? new HashSet<>() : authorAssembler.fromDto(dto.getAuthors()))
                .medias(dto.getMedias() == null ? new HashSet<>() : mediaAssembler.fromDto(dto.getMedias()))
                .tags(dto.getTags() == null ? new HashSet<>() : tagAssembler.fromDto(dto.getTags()))
                .categories(dto.getCategories() == null ? new HashSet<>() : categoryAssembler.fromDto(dto.getCategories()))
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
        return PostDto.builder()
                .id(postDraft.getId())
                .title(postDraft.getTitle())
                .content(postDraft.getContent())
                .medias(mediaAssembler.fromEntity(postDraft.getMedias()))
                .tags(tagAssembler.fromEntity(postDraft.getTags()))
                .categories(categoryAssembler.fromEntity(postDraft.getCategories()))
                .build();
    }

    public PostDraft fromPostDto(PostDto postDto) {
        return PostDraft.builder()
                .id(postDto.getId())
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .medias(mediaAssembler.fromDto(postDto.getMedias()))
                .tags(tagAssembler.fromDto(postDto.getTags()))
                .categories(categoryAssembler.fromDto(postDto.getCategories()))
                .build();
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
