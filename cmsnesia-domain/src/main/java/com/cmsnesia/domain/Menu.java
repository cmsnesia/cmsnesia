package com.cmsnesia.domain;

import com.cmsnesia.domain.model.Category;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "menu")
@EqualsAndHashCode(callSuper = true)
public class Menu extends Audit {

  @Id private String id;

  @NotBlank(message = "Menu name must be not blank")
  @Size(max = 50)
  @Indexed
  private String name;

  @NotEmpty private Set<Category> categories;
}
