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
@RequiredArgsConstructor
public class JpaRefreshTokenProvider implements CodeFortressRefreshTokenProvider {

    private final RefreshTokenRepository tokenRepository;
    private final SecurityUserRepository userRepository;
    private final CodeFortressProperties properties;

    @Override
    @Transactional
    public CodeFortressRefreshToken create(String username, long expirationMs) {
        SecurityUserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int maxSessions = properties.getSecurity().getRefreshToken().getMaxSessions();

        // CASO A: Single Session Estricta (Optimización rápida)
        if (maxSessions == 1) {
            tokenRepository.deleteByUser(user);
            tokenRepository.flush(); // Forzar borrado inmediato
        }
        // CASO B: Límite Numérico (Netflix Style)
        else if (maxSessions > 1) {
            List<RefreshTokenEntity> activeTokens = tokenRepository.findByUserOrderByIdAsc(user);

            // Si ya estamos llenos (o pasados), hay que hacer espacio
            int excess = activeTokens.size() - maxSessions + 1; // +1 porque vamos a agregar uno nuevo ahora

            if (excess > 0) {
                // Borramos los 'N' más viejos de la lista
                for (int i = 0; i < excess; i++) {
                    tokenRepository.delete(activeTokens.get(i));
                }
                tokenRepository.flush();
            }
        }
        // CASO C: Ilimitado (-1) -> No hacemos nada, solo insertamos.

        // --- CREACIÓN (Igual que siempre) ---
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUser(user);
        entity.setExpiryDate(Instant.now().plusMillis(expirationMs));
        entity.setToken(UUID.randomUUID().toString());

        entity = tokenRepository.save(entity);

        return new CodeFortressRefreshToken(entity.getToken(), user.getUsername(), entity.getExpiryDate());
    }

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

    @Override
    @Transactional
    public void deleteByToken(String token) {
        tokenRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        userRepository.findByUsername(username)
                .ifPresent(tokenRepository::deleteByUser);
    }
}