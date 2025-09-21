package ru.practicum.ewm.stats.server.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidIpAddressValidator implements ConstraintValidator<ValidIpAddress, String> {

    // Regex for basic IPv4 validation
    private static final String IPV4_REGEX =
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    private Pattern pattern;

    @Override
    public void initialize(ValidIpAddress constraintAnnotation) {
        pattern = Pattern.compile(IPV4_REGEX);
    }

    @Override
    public boolean isValid(String ipAddress, ConstraintValidatorContext context) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return true; // Consider null/empty as valid if not also marked @NotNull or @NotBlank
        }
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }
}