package edu.java.annotation;

import edu.java.util.URIValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = URIValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface URIConstraint {
    String message() default "Invalid URI";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
