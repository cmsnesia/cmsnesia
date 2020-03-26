package com.cmsnesia.service.command;

import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

public abstract class AbstractCommand<T, R> implements Command<T, R> {

  @Autowired private Validator validator;

  @Override
  public Mono<Void> validate(Object request) {
    Set<ConstraintViolation<Object>> violations = validator.validate(request);
    if (!violations.isEmpty()) {
      throw new RuntimeException(violations.toString());
    }
    return Mono.empty();
  }
}
