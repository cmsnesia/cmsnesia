package com.cmsnesia.domain;

import com.cmsnesia.domain.model.Email;
import com.cmsnesia.domain.model.Token;
import com.cmsnesia.domain.validator.EmailCollection;
import com.cmsnesia.domain.validator.Password;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "auth")
@EqualsAndHashCode(callSuper = true)
public class Auth extends Audit {

  @Id private String id;

  @Indexed(unique = true)
  @NotBlank(message = "Username must be not blank")
  @Size(max = 25)
  private String username;

  @Password private String password;

  @NotEmpty private Set<@NotNull String> roles;

  @NotBlank(message = "Full name must be not blank")
  @Size(max = 50)
  private String fullName;

  @NotEmpty
  @EmailCollection(message = "Invalid email")
  private Set<Email> emails;

  @NotNull private Set<Token> tokens;
}
