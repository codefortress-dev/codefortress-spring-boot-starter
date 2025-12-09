package dev.codefortress.core.spi;

import dev.codefortress.core.model.CodeFortressRefreshToken;
import java.util.Optional;

public interface CodeFortressRefreshTokenProvider {
    CodeFortressRefreshToken create(String username, long expirationMs);
    Optional<CodeFortressRefreshToken> findByToken(String token);
    void deleteByToken(String token);
    void deleteByUsername(String username);
}