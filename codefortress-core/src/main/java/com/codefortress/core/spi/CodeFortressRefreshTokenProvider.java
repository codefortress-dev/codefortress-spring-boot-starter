package com.codefortress.core.spi;

import com.codefortress.core.model.CodeFortressRefreshToken;
import java.util.Optional;

public interface CodeFortressRefreshTokenProvider {
    CodeFortressRefreshToken create(String username, long expirationMs);
    Optional<CodeFortressRefreshToken> findByToken(String token);
    void deleteByToken(String token); // Para Logout o Rotación
    void deleteByUsername(String username); // Para limpiar sesiones viejas
}