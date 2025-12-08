package com.codefortress.jpa.adapter;

import com.codefortress.core.model.CodeFortressRefreshToken;
import com.codefortress.core.spi.CodeFortressRefreshTokenProvider;
import com.codefortress.jpa.entity.RefreshTokenEntity;
import com.codefortress.jpa.entity.SecurityUserEntity;
import com.codefortress.jpa.repository.RefreshTokenRepository;
import com.codefortress.jpa.repository.SecurityUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JpaRefreshTokenProvider implements CodeFortressRefreshTokenProvider {

    private final RefreshTokenRepository tokenRepository;
    private final SecurityUserRepository userRepository;

    @Override
    @Transactional
    public CodeFortressRefreshToken create(String username, long expirationMs) {
        // 1. Buscar usuario
        SecurityUserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found for refresh token"));

        // 2. Generar Token Opaco (UUID)
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