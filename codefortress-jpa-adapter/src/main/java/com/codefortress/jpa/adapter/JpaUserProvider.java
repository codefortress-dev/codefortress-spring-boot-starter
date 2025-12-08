package com.codefortress.jpa.adapter;

import com.codefortress.core.exception.UserAlreadyExistsException;
import com.codefortress.core.model.CodeFortressUser;
import com.codefortress.core.spi.CodeFortressUserProvider;
import com.codefortress.jpa.entity.SecurityRoleEntity;
import com.codefortress.jpa.entity.SecurityUserEntity;
import com.codefortress.jpa.repository.SecurityUserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;



public class JpaUserProvider implements CodeFortressUserProvider {

    private final SecurityUserRepository userRepository;

    // Constructor manual
    public JpaUserProvider(SecurityUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CodeFortressUser> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapToCoreUser);
    }

    @Override
    @Transactional
    public CodeFortressUser save(CodeFortressUser user) {
        // VALIDACIÓN
        if (userRepository.findByUsername(user.username()).isPresent()) {
            throw new UserAlreadyExistsException(user.username());
        }

        SecurityUserEntity entity = new SecurityUserEntity();
        entity.setUsername(user.username());
        entity.setPassword(user.password());
        entity.setEnabled(user.enabled());

        // Mapeo simple de roles (String -> Entity)
        // En un caso real, buscaríamos si el rol ya existe para no duplicarlo
        // Aquí simplificamos creando roles nuevos al vuelo
        user.roles().forEach(roleName -> {
            SecurityRoleEntity roleEntity = new SecurityRoleEntity();
            roleEntity.setName(roleName);
            entity.getRoles().add(roleEntity);
        });

        SecurityUserEntity saved = userRepository.save(entity);
        return mapToCoreUser(saved);
    }

    // Mapper privado: Entity -> Record (Core)
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