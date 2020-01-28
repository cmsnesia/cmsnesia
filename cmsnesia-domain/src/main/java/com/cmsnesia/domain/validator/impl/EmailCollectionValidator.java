package com.cmsnesia.domain.validator.impl;

import com.cmsnesia.domain.model.Email;
import com.cmsnesia.domain.validator.EmailCollection;
import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class EmailCollectionValidator
    implements ConstraintValidator<EmailCollection, Collection<Email>> {

  @Override
  public boolean isValid(Collection<Email> values, ConstraintValidatorContext context) {
    if (values == null || values.isEmpty()) {
      return false;
    }
    boolean noneMatch =
        values.stream()
            .noneMatch(
                email -> {
                  if (email.getTypes().isEmpty()) {
                    return true;
                  } else {
                    email.getTypes().stream()
                        .noneMatch(
                            type -> {
                              return !StringUtils.isEmpty(type);
                            });
                  }
                  return !StringUtils.isEmpty(email.getAddress())
                      || !StringUtils.isEmpty(email.getStatus());
                });
    return !noneMatch;
  }
}
