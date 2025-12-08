package com.codefortress.core.service;

import com.codefortress.core.config.CodeFortressProperties;
import com.codefortress.core.exception.CodeFortressException;
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

        // 1. Validación de Longitud (Universal y rápida)
        if (password.length() < rules.getMinLength()) {
            throw new CodeFortressException("La contraseña debe tener al menos " + rules.getMinLength() + " caracteres.");
        }

        // 2. ¿El usuario configuró su propio Patrón Maestro?
        if (rules.getRegexp() != null && !rules.getRegexp().isBlank()) {
            if (!Pattern.matches(rules.getRegexp(), password)) {
                // Lanzamos el mensaje personalizado que el usuario configuró
                throw new CodeFortressException(rules.getRegexpErrorMessage());
            }
            // Si hay Regex y pasa, retornamos. La Regex tiene prioridad sobre los booleanos.
            return;
        }

        // 3. Fallback: Si NO hay Regex, usamos las reglas booleanas simples
        if (rules.isRequireUppercase() && !password.chars().anyMatch(Character::isUpperCase)) {
            throw new CodeFortressException("La contraseña debe contener al menos una letra mayúscula.");
        }
        if (rules.isRequireNumbers() && !password.chars().anyMatch(Character::isDigit)) {
            throw new CodeFortressException("La contraseña debe contener al menos un número.");
        }
    }
}