package com.codefortress.core.service;

import com.codefortress.core.config.CodeFortressProperties;
import com.codefortress.core.exception.CodeFortressException; // Reutilizamos nuestra base
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordValidator {

    private final CodeFortressProperties properties;

    public void validate(String password) {
        CodeFortressProperties.Password rules = properties.getPassword();

        if (password == null || password.length() < rules.getMinLength()) {
            throw new CodeFortressException("La contraseña debe tener al menos " + rules.getMinLength() + " caracteres.");
        }

        if (rules.isRequireUppercase() && !password.chars().anyMatch(Character::isUpperCase)) {
            throw new CodeFortressException("La contraseña debe contener al menos una letra mayúscula.");
        }

        if (rules.isRequireLowercase() && !password.chars().anyMatch(Character::isLowerCase)) {
            throw new CodeFortressException("La contraseña debe contener al menos una letra minúscula.");
        }

        if (rules.isRequireNumbers() && !password.chars().anyMatch(Character::isDigit)) {
            throw new CodeFortressException("La contraseña debe contener al menos un número.");
        }

        if (rules.isRequireSpecialChar() && password.matches("[A-Za-z0-9 ]*")) {
            throw new CodeFortressException("La contraseña debe contener un carácter especial.");
        }
    }
}