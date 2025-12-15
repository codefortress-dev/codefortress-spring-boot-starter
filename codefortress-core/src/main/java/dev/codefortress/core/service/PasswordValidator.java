package dev.codefortress.core.service;

import dev.codefortress.core.config.CodeFortressProperties;
import dev.codefortress.core.exception.CodeFortressException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Service for validating passwords based on the configured password policy.
 */
@Service
@RequiredArgsConstructor
public class PasswordValidator {

    private final CodeFortressProperties properties;

    /**
     * Validates a password against the configured password policy.
     *
     * @param password the password to validate
     * @throws CodeFortressException if the password is not valid
     */
    public void validate(String password) {
        CodeFortressProperties.Password rules = properties.getPassword();

        if (password == null) {
            throw new CodeFortressException("La contraseña no puede estar vacía.");
        }

        if (password.length() < rules.getMinLength()) {
            throw new CodeFortressException("La contraseña debe tener al menos " + rules.getMinLength() + " caracteres.");
        }

        if (rules.getRegexp() != null && !rules.getRegexp().isBlank()) {
            if (!Pattern.matches(rules.getRegexp(), password)) {
                throw new CodeFortressException(rules.getRegexpErrorMessage());
            }
            return;
        }

        if (rules.isRequireUppercase() && !password.chars().anyMatch(Character::isUpperCase)) {
            throw new CodeFortressException("La contraseña debe contener al menos una letra mayúscula.");
        }
        if (rules.isRequireNumbers() && !password.chars().anyMatch(Character::isDigit)) {
            throw new CodeFortressException("La contraseña debe contener al menos un número.");
        }
    }
}