package dev.example.custom;

import dev.codefortress.core.model.CodeFortressRefreshToken;
import dev.codefortress.core.spi.CodeFortressRefreshTokenProvider;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class MyCustomTokenStore implements CodeFortressRefreshTokenProvider {

    private final ConcurrentMap<String, CodeFortressRefreshToken> byToken = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> tokenByUsername = new ConcurrentHashMap<>();

    @Override
    public CodeFortressRefreshToken create(String username, long expirationMs) {
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusMillis(expirationMs);

        CodeFortressRefreshToken refreshToken =
                new CodeFortressRefreshToken(token, username, expiry);

        // si solo permites 1 refresh token activo por usuario, elimina el anterior
        String oldToken = tokenByUsername.put(username, token);
        if (oldToken != null) {
            byToken.remove(oldToken);
        }

        byToken.put(token, refreshToken);
        return refreshToken;
    }

    @Override
    public Optional<CodeFortressRefreshToken> findByToken(String token) {
        return Optional.ofNullable(byToken.get(token));
    }

    @Override
    public void deleteByToken(String token) {
        CodeFortressRefreshToken removed = byToken.remove(token);
        if (removed != null) {
            tokenByUsername.remove(removed.username(), token);
        }
    }

    @Override
    public void deleteByUsername(String username) {
        String token = tokenByUsername.remove(username);
        if (token != null) {
            byToken.remove(token);
        }
    }
}
