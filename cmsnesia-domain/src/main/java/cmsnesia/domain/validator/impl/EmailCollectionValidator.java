package cmsnesia.domain.validator.impl;

import cmsnesia.domain.model.Email;
import cmsnesia.domain.validator.EmailCollection;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

public class EmailCollectionValidator implements ConstraintValidator<EmailCollection, Collection<Email>> {

    @Override
    public boolean isValid(Collection<Email> values, ConstraintValidatorContext context) {
        if (values == null || values.isEmpty()) {
            return false;
        }
        boolean noneMatch = values.stream().noneMatch(email -> {
            if (email.getTypes().isEmpty()) {
                return true;
            } else {
                email.getTypes().stream().noneMatch(type -> {
                    return !StringUtils.isEmpty(type);
                });
            }
            return !StringUtils.isEmpty(email.getAddress()) || !StringUtils.isEmpty(email.getStatus());
        });
        return !noneMatch;
    }

}