package id.or.gri.domain.validator;

import id.or.gri.domain.validator.impl.EmailCollectionValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailCollectionValidator.class)
@Documented
public @interface EmailCollection {

    String message() default "Invalid emails";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}