package com.cmsnesia.domain;

import com.cmsnesia.domain.model.Media;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "event")
@EqualsAndHashCode(callSuper = true)
public class Event extends Audit {

  @Id private String id;

  @NotBlank(message = "Event name must be not blank")
  @Size(max = 50)
  @Indexed
  private String name;

  @NotBlank(message = "Event description must be not empty")
  private String description;

  @NotNull(message = "Event start date must be not null")
  private Date startedAt;

  @NotNull(message = "Event end date must be not null")
  private Date endedAt;

  @NotEmpty(message = "Event types must be not empty")
  @Indexed
  private Set<String> types;

  @NotEmpty(message = "Event media must be not empty")
  private Set<Media> medias;
}
