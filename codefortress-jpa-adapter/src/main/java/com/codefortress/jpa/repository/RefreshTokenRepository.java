package com.codefortress.jpa.repository;

import com.codefortress.jpa.entity.RefreshTokenEntity;
import com.codefortress.jpa.entity.SecurityUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    void deleteByUser(SecurityUserEntity user); // Útil para "Cerrar sesión en todos los dispositivos"

    @Modifying
    void deleteByToken(String token);
}