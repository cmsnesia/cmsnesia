package com.cmsnesia.domain;

import com.cmsnesia.domain.model.Author;
import com.cmsnesia.domain.model.Category;
import com.cmsnesia.domain.model.Media;
import com.cmsnesia.domain.model.Tag;
import java.util.Set;
import javax.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "post")
@EqualsAndHashCode(callSuper = true)
public class Post extends Audit {

  @Id private String id;

  @NotBlank(message = "Post title must be not blank")
  @Size(max = 100, message = "Maximum size of post title is 100")
  private String title;

  @NotBlank(message = "Post content must be not blank")
  private String content;

  @NotEmpty private Set<Author> authors;

  @NotNull private Set<Media> medias;

  @NotEmpty private Set<Tag> tags;

  @NotEmpty private Set<Category> categories;

  @Min(value = 0, message = "Post view count must be greater or equal to 0")
  private Long viewCount;

  @Min(value = 0, message = "Post like count must be greater or equal to 0")
  private Long likeCount;

  @Min(value = 0, message = "Post dislike count must be greater or equal to 0")
  private Long dislikeCount;

  @Min(value = 0, message = "Post share count must be greater or equal to 0")
  private Long shareCount;
}
