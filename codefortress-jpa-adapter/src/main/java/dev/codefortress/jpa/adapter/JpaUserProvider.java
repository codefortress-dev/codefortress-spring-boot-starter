package dev.codefortress.jpa.adapter;

import dev.codefortress.core.exception.UserAlreadyExistsException;
import dev.codefortress.core.model.CodeFortressUser;
import dev.codefortress.core.spi.CodeFortressUserProvider;
import dev.codefortress.jpa.entity.SecurityRoleEntity;
import dev.codefortress.jpa.entity.SecurityUserEntity;
import dev.codefortress.jpa.repository.SecurityRoleRepository;
import dev.codefortress.jpa.repository.SecurityUserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JPA implementation of the {@link CodeFortressUserProvider} SPI.
 * This class provides user data from a JPA-based repository.
 */
public class JpaUserProvider implements CodeFortressUserProvider {

    private final SecurityUserRepository userRepository;
    private final SecurityRoleRepository roleRepository;

    public JpaUserProvider(SecurityUserRepository userRepository, SecurityRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an optional containing the user if found, or an empty optional otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CodeFortressUser> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapToCoreUser);
    }

    /**
     * Saves a user.
     *
     * @param user the user to save
     * @return the saved user
     * @throws UserAlreadyExistsException if the user already exists
     */
    @Override
    @Transactional
    public CodeFortressUser save(CodeFortressUser user) {
        if (userRepository.findByUsername(user.username()).isPresent()) {
            throw new UserAlreadyExistsException(user.username());
        }
        SecurityUserEntity entity = new SecurityUserEntity();
        entity.setUsername(user.username());
        entity.setPassword(user.password());
        entity.setEnabled(user.enabled());

        if (user.roles() != null && !user.roles().isEmpty()) {
            Set<SecurityRoleEntity> managedRoles = user.roles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseGet(() -> {
                                SecurityRoleEntity newRole = new SecurityRoleEntity();
                                newRole.setName(roleName);
                                return roleRepository.save(newRole);
                            }))
                    .collect(Collectors.toSet());

            entity.setRoles(managedRoles);
        } else {
            entity.setRoles(new HashSet<>());
        }
        SecurityUserEntity saved = userRepository.save(entity);
        return mapToCoreUser(saved);
    }

    private CodeFortressUser mapToCoreUser(SecurityUserEntity entity) {
        return new CodeFortressUser(
                entity.getUsername(),
                entity.getPassword(),
                entity.getRoles().stream()
                        .map(SecurityRoleEntity::getName)
                        .collect(Collectors.toSet()),
                entity.isEnabled()
        );
    }
}