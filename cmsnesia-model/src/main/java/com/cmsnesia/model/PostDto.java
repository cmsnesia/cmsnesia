package com.cmsnesia.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto extends BaseDto {

  private String id;
  private String title;
  private String content;

  private Set<AuthorDto> authors;
  private Set<MediaDto> medias;
  private Set<TagDto> tags;
  private Set<CategoryDto> categories;

  private Long viewCount;
  private Long likeCount;
  private Long dislikeCount;
  private Long shareCount;
}
