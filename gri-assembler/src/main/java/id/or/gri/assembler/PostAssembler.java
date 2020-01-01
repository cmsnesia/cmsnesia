package id.or.gri.assembler;

import id.or.gri.domain.Post;
import id.or.gri.model.PostDto;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
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
    public Post
    fromDto(@Nonnull PostDto dto) {
        return Post.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .authors(authorAssembler.fromDto(dto.getAuthors()))
                .medias(mediaAssembler.fromDto(dto.getMedias()))
                .tags(tagAssembler.fromDto(dto.getTags()))
                .categories(categoryAssembler.fromDto(dto.getCategories()))
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
                .authors(authorAssembler.fromEntity(entity.getAuthors()))
                .medias(mediaAssembler.fromEntity(entity.getMedias()))
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
}
