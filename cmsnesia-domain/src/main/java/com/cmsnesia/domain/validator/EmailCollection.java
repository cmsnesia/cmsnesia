package com.cmsnesia.domain.validator;

import com.cmsnesia.domain.validator.impl.EmailCollectionValidator;
import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailCollectionValidator.class)
@Documented
public @interface EmailCollection {

  String message() default "Invalid emails";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
