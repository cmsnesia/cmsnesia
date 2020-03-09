package com.cmsnesia.domain.model;

import java.io.Serializable;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Phone implements Serializable {

  private String number;
  private Set<String> types;
  private String status;
}
