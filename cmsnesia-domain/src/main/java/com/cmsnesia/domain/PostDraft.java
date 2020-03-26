package com.cmsnesia.domain;

import com.cmsnesia.domain.model.Category;
import com.cmsnesia.domain.model.Media;
import com.cmsnesia.domain.model.Tag;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cmsnesia.domain.validator.UrlMustValid;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "post_draft")
@EqualsAndHashCode(callSuper = true)
public class PostDraft extends Audit {

  @Id private String id;

  @UrlMustValid(message = "Invalid URL")
  private String link;

  @NotBlank(message = "Post title must be not blank")
  @Size(max = 100, message = "Maximum size of post title is 100")
  @Indexed
  private String title;

  @NotBlank(message = "Post content must be not blank")
  private String content;

  @NotNull(message = "Medias must be not null")
  private Set<Media> medias;

  @NotEmpty(message = "Tags is required")
  private Set<Tag> tags;

  @NotEmpty(message = "Categories is required")
  private Set<Category> categories;
}
