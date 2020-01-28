package com.cmsnesia.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDto implements Serializable {

  private String id;
  private String name;
  private String createdAt;
  private String createdBy;
}
