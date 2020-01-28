package com.cmsnesia.domain.model;

import com.cmsnesia.domain.model.enums.MediaType;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Media implements Serializable {

  private String name;
  private String url;
  private MediaType type;
}
