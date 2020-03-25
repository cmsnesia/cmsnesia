package com.cmsnesia.domain.validator.impl;

import com.cmsnesia.domain.validator.AbstractReactiveConstraintValidator;
import com.cmsnesia.domain.validator.PasswordMustSecure;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintValidatorContext;

public class PasswordMustSecureImpl
    extends AbstractReactiveConstraintValidator<PasswordMustSecure, CharSequence> {

  @Override
  public Mono<Boolean> validate(
      CharSequence value, PasswordMustSecure annotation, ConstraintValidatorContext context) {
    if (value.length() < 6) {
      return Mono.just(false);
    }
    return Mono.just(true);
  }
}
