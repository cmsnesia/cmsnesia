package com.cmsnesia.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuDto extends BaseDto {

  private String id;
  private String name;
  private Set<CategoryDto> categories;
}
