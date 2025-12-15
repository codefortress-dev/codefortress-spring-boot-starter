package dev.codefortress.core.exception;

/**
 * An exception that is thrown when a user already exists.
 */
public class UserAlreadyExistsException extends CodeFortressException {
    public UserAlreadyExistsException(String username) {
        super("El usuario '" + username + "' ya existe.");
    }
}