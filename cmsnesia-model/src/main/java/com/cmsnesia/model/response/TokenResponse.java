package com.cmsnesia.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {

  private String accessToken;
  private String refreshToken;
  private String tokenType;
}
