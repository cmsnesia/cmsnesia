package com.cmsnesia.domain.validator;

import com.cmsnesia.domain.validator.impl.PasswordMustSecureImpl;
import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMustSecureImpl.class)
@Documented
public @interface PasswordMustSecure {

  String message() default "Your password isn't secure, please provide secure password instead";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
