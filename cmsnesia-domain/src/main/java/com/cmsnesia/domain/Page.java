package com.cmsnesia.domain;

import com.cmsnesia.domain.model.Author;
import com.cmsnesia.domain.model.Media;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "page")
@EqualsAndHashCode(callSuper = true)
public class Page extends Audit {

  @Id private String id;

  @NotBlank(message = "Page name must be not blank")
  @Size(max = 100, message = "Maximum size of post title is 100")
  @Indexed(unique = true)
  private String name;

  @NotBlank(message = "Menu name must be not blank")
  @Size(max = 50)
  @Indexed(unique = true)
  private String content;

  @NotNull private Set<Media> medias;
  @NotEmpty private Set<Author> authors;
}
