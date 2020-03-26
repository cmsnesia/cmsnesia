package com.cmsnesia.domain.validator.impl;

import com.cmsnesia.domain.validator.AbstractReactiveConstraintValidator;
import com.cmsnesia.domain.validator.UrlMustValid;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class UrlMustValidImpl extends AbstractReactiveConstraintValidator<UrlMustValid, String> {

  @Override
  public Mono<Boolean> validate(
      String value, UrlMustValid annotation, ConstraintValidatorContext context) {
    if (StringUtils.isEmpty(value) || value.length() > 100) {
      return Mono.just(false);
    }
    Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
    return Mono.just(pattern.matcher(value).matches());
  }
}
