package dev.codefortress.core.service;

import dev.codefortress.core.config.CodeFortressProperties;
import dev.codefortress.core.exception.CodeFortressException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PasswordValidator {

    private final CodeFortressProperties properties;

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