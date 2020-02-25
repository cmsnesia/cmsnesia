package com.cmsnesia.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "category")
@EqualsAndHashCode(callSuper = true)
public class Category extends Audit {

  @Id private String id;

  @NotBlank(message = "Category name must be not blank")
  @Size(max = 50)
  @Indexed
  private String name;
}
