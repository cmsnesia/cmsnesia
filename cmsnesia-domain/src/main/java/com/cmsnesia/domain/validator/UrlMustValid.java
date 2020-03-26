package com.cmsnesia.domain.validator;

import com.cmsnesia.domain.validator.impl.UrlMustValidImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UrlMustValidImpl.class)
@Documented
public @interface UrlMustValid {

  String message() default "Invalid URL";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
