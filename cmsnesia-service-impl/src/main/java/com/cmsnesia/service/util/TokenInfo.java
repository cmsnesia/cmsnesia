package com.cmsnesia.service.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "cmsnesia.token")
public class TokenInfo {

  private String issuer;
  private Long accessTokenExparation;
  private Long refreshTokenExparation;
  private String secret;
  private Long max;
}
