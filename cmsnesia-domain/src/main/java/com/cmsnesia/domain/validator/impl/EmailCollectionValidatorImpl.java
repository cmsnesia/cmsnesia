package com.cmsnesia.domain.validator.impl;

import com.cmsnesia.domain.model.Email;
import java.util.Collection;
import javax.validation.ConstraintValidatorContext;

import com.cmsnesia.domain.validator.AbstractReactiveConstraintValidator;
import com.cmsnesia.domain.validator.EmailCollectionMustValid;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

public class EmailCollectionValidatorImpl
    extends AbstractReactiveConstraintValidator<EmailCollectionMustValid, Collection<Email>> {

  @Override
  public Mono<Boolean> validate(
      Collection<Email> values,
      EmailCollectionMustValid annotation,
      ConstraintValidatorContext context) {
    if (values == null || values.isEmpty()) {
      return Mono.just(false);
    }
    boolean noneMatch =
        values.stream()
            .noneMatch(
                email -> {
                  if (email.getTypes().isEmpty()) {
                    return true;
                  } else {
                    email.getTypes().stream().noneMatch(type -> !StringUtils.isEmpty(type));
                  }
                  return !StringUtils.isEmpty(email.getAddress())
                      || !StringUtils.isEmpty(email.getStatus());
                });
    return Mono.just(!noneMatch);
  }
}
