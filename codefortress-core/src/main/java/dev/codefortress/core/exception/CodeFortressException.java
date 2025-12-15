package dev.codefortress.core.exception;

/**
 * A generic exception for the CodeFortress library.
 */
public class CodeFortressException extends RuntimeException {
    public CodeFortressException(String message) {
        super(message);
    }
}
