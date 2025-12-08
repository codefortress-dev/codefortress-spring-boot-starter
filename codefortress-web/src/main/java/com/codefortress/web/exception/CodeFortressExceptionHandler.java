package com.codefortress.web.exception;

import com.codefortress.core.exception.CodeFortressException;
import com.codefortress.core.exception.UserAlreadyExistsException;
import com.codefortress.web.dto.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE) // Capturamos nuestras excepciones antes que nadie
public class CodeFortressExceptionHandler {

    // 1. Manejo de Usuario Duplicado (409 Conflict)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserExists(UserAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 2. Manejo de Credenciales Inválidas (401 Unauthorized)
    // Spring Security lanza esto cuando falla el authenticationManager.authenticate()
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Usuario o contraseña incorrectos",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // 3. Fallback para otras excepciones de nuestra librería
    @ExceptionHandler(CodeFortressException.class)
    public ResponseEntity<ErrorResponse> handleGeneric(CodeFortressException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
