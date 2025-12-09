package dev.codefortress.jpa.repository;

import dev.codefortress.jpa.entity.RefreshTokenEntity;
import dev.codefortress.jpa.entity.SecurityUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    List<RefreshTokenEntity> findByUserOrderByIdAsc(SecurityUserEntity user);
    @Modifying
    void deleteByUser(SecurityUserEntity user);

    @Modifying
    void deleteByToken(String token);
}