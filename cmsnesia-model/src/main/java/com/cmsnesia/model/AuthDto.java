package com.cmsnesia.model;

import java.io.Serializable;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDto implements Serializable {

  private String id;

  private String username;

  private String password;

  private Set<String> roles;

  private String fullName;

  private Set<EmailDto> emails;
}
