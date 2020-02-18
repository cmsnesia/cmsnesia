package com.cmsnesia.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class BaseDto implements Serializable {

  private Set<ApplicationDto> applications;
}
