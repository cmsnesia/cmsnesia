package com.cmsnesia.domain.validator;

import com.cmsnesia.domain.validator.impl.EmailCollectionValidatorImpl;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailCollectionValidatorImpl.class)
@Documented
public @interface EmailCollectionMustValid {

  String message() default "Invalid emails";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
