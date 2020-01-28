package com.cmsnesia.domain.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token implements Serializable {

  private String accessToken;
  private String refreshToken;
  private String tokenType;
}
