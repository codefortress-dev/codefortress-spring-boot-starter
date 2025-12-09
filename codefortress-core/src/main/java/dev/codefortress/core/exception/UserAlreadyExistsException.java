package dev.codefortress.core.exception;

public class UserAlreadyExistsException extends CodeFortressException {
    public UserAlreadyExistsException(String username) {
        super("El usuario '" + username + "' ya existe.");
    }
}