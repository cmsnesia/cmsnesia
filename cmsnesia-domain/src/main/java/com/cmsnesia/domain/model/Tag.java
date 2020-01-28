package com.cmsnesia.domain.model;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tag {

  @NotBlank(message = "Tag name must be not blank")
  @Size(max = 25)
  private String name;

  @NotNull private Date createdAt;

  @NotNull private String createdBy;
}
