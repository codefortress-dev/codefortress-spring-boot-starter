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



public class JpaUserProvider implements CodeFortressUserProvider {

    private final SecurityUserRepository userRepository;
    private final SecurityRoleRepository roleRepository;

    // Constructor manual
    public JpaUserProvider(SecurityUserRepository userRepository, SecurityRoleRepository roleRepository) {
        this.userRepository = userRepository;
         this.roleRepository =  roleRepository;
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
        // 1. VALIDACIÓN
        if (userRepository.findByUsername(user.username()).isPresent()) {
            throw new UserAlreadyExistsException(user.username());
        }

        SecurityUserEntity entity = new SecurityUserEntity();
        entity.setUsername(user.username());
        entity.setPassword(user.password());
        entity.setEnabled(user.enabled());

        // 2. LÓGICA DE ROLES (CORREGIDA)
        // En lugar de crear ciegamente, buscamos si existe.
        if (user.roles() != null && !user.roles().isEmpty()) {
            Set<SecurityRoleEntity> managedRoles = user.roles().stream()
                    .map(roleName -> roleRepository.findByName(roleName) // ¿Existe en BD?
                            .orElseGet(() -> {
                                // No existe -> Lo creamos y guardamos ahora mismo
                                SecurityRoleEntity newRole = new SecurityRoleEntity();
                                newRole.setName(roleName);
                                return roleRepository.save(newRole);
                            }))
                    .collect(Collectors.toSet());

            entity.setRoles(managedRoles);
        } else {
            entity.setRoles(new HashSet<>());
        }

        // 3. GUARDAR USUARIO
        SecurityUserEntity saved = userRepository.save(entity);

        // 4. RETORNAR (Usando tu mapper privado)
        return mapToCoreUser(saved);
    }

    // Mapper privado: Entity -> Record (Core)
    // Se mantiene intacto como lo tenías
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