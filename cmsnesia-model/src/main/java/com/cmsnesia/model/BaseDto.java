package com.cmsnesia.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
// @AllArgsConstructor
// @NoArgsConstructor
// @Builder
public class BaseDto implements Serializable {

  private Set<ApplicationDto> applications;
}
