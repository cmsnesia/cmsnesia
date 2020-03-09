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
public class CategoryGroupDto extends BaseDto {

  private String id;
  private String name;
  private Set<String> categoryIds;
}
