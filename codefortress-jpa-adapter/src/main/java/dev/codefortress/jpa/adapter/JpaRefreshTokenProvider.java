package dev.codefortress.jpa.adapter;

import dev.codefortress.core.model.CodeFortressRefreshToken;
import dev.codefortress.core.spi.CodeFortressRefreshTokenProvider;
import dev.codefortress.jpa.entity.RefreshTokenEntity;
import dev.codefortress.jpa.entity.SecurityUserEntity;
import dev.codefortress.jpa.repository.RefreshTokenRepository;
import dev.codefortress.jpa.repository.SecurityUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.codefortress.core.config.CodeFortressProperties;

/**
 * JPA implementation of the {@link CodeFortressRefreshTokenProvider} SPI.
 * This class provides refresh token data from a JPA-based repository.
 */
@RequiredArgsConstructor
public class JpaRefreshTokenProvider implements CodeFortressRefreshTokenProvider {

    private final RefreshTokenRepository tokenRepository;
    private final SecurityUserRepository userRepository;
    private final CodeFortressProperties properties;

    /**
     * Creates a new refresh token for the given user.
     * It also handles session management by deleting old tokens if the maximum number of sessions is exceeded.
     *
     * @param username     the username of the user
     * @param expirationMs the expiration time in milliseconds
     * @return the created refresh token
     */
    @Override
    @Transactional
    public CodeFortressRefreshToken create(String username, long expirationMs) {
        SecurityUserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int maxSessions = properties.getSecurity().getRefreshToken().getMaxSessions();

        if (maxSessions == 1) {
            tokenRepository.deleteByUser(user);
            tokenRepository.flush();
        } else if (maxSessions > 1) {
            List<RefreshTokenEntity> activeTokens = tokenRepository.findByUserOrderByIdAsc(user);
            int excess = activeTokens.size() - maxSessions + 1;

            if (excess > 0) {
                for (int i = 0; i < excess; i++) {
                    tokenRepository.delete(activeTokens.get(i));
                }
                tokenRepository.flush();
            }
        }

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUser(user);
        entity.setExpiryDate(Instant.now().plusMillis(expirationMs));
        entity.setToken(UUID.randomUUID().toString());

        entity = tokenRepository.save(entity);

        return new CodeFortressRefreshToken(entity.getToken(), user.getUsername(), entity.getExpiryDate());
    }

    /**
     * Finds a refresh token by its token string.
     *
     * @param token the token string to search for
     * @return an optional containing the refresh token if found, or an empty optional otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CodeFortressRefreshToken> findByToken(String token) {
        return tokenRepository.findByToken(token)
                .map(entity -> new CodeFortressRefreshToken(
                        entity.getToken(),
                        entity.getUser().getUsername(),
                        entity.getExpiryDate()
                ));
    }

    /**
     * Deletes a refresh token by its token string.
     *
     * @param token the token string of the refresh token to delete
     */
    @Override
    @Transactional
    public void deleteByToken(String token) {
        tokenRepository.deleteByToken(token);
    }

    /**
     * Deletes all refresh tokens for a given user.
     *
     * @param username the username of the user
     */
    @Override
    @Transactional
    public void deleteByUsername(String username) {
        userRepository.findByUsername(username)
                .ifPresent(tokenRepository::deleteByUser);
    }
}