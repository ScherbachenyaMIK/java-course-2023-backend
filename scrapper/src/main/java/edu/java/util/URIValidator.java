package edu.java.util;

import edu.java.annotation.URIConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;

public class URIValidator implements ConstraintValidator<URIConstraint, URI> {
    @Override
    public void initialize(URIConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(URI value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        String urlString = value.toString();
        return urlString.matches("^https?://api\\.github\\.com/repos/[^/]+/[^/]+$"
            + "|^https?://api\\.stackexchange\\.com/2\\.2/questions"
            + "/[0-9]+\\?order=desc&sort=activity&site=stackoverflow$");
    }
}
