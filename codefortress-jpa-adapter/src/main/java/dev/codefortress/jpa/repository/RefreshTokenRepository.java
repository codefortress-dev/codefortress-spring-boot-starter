package dev.codefortress.jpa.repository;

import dev.codefortress.jpa.entity.RefreshTokenEntity;
import dev.codefortress.jpa.entity.SecurityUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for {@link RefreshTokenEntity}.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    /**
     * Finds a refresh token by its token string.
     *
     * @param token the token string to search for
     * @return an optional containing the refresh token if found, or an empty optional otherwise
     */
    Optional<RefreshTokenEntity> findByToken(String token);

    /**
     * Finds all refresh tokens for a given user, ordered by ID.
     *
     * @param user the user to search for
     * @return a list of refresh tokens
     */
    List<RefreshTokenEntity> findByUserOrderByIdAsc(SecurityUserEntity user);

    /**
     * Deletes all refresh tokens for a given user.
     *
     * @param user the user
     */
    @Modifying
    void deleteByUser(SecurityUserEntity user);

    /**
     * Deletes a refresh token by its token string.
     *
     * @param token the token string of the refresh token to delete
     */
    @Modifying
    void deleteByToken(String token);
}