package com.cmsnesia.domain;

import com.cmsnesia.domain.model.Author;
import com.cmsnesia.domain.model.Media;
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
@Document(collection = "page")
@EqualsAndHashCode(callSuper = true)
public class Page extends Audit {

  @Id private String id;

  @UrlMustValid(message = "Invalid URL")
  private String link;

  @NotBlank(message = "Page name must be not blank")
  @Size(max = 100, message = "Maximum size of post title is 100")
  @Indexed
  private String name;

  @NotBlank(message = "Menu name must be not blank")
  @Size(max = 50)
  private String content;

  @NotNull private Set<Media> medias;
  @NotEmpty private Set<Author> authors;
}
