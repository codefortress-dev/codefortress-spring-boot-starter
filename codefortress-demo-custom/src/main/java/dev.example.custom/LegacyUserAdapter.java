package dev.example.custom;


import dev.codefortress.core.model.CodeFortressUser;
import dev.codefortress.core.spi.CodeFortressUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LegacyUserAdapter implements CodeFortressUserProvider {

    private final EmpleadoLegacyRepository repository; // Interfaz JPA estándar

    @Override
    public Optional<CodeFortressUser> findByUsername(String username) {
        // Adaptamos: EmpleadoLegacy -> CodeFortressUser
        return repository.findByEmail(username)
                .map(emp -> new CodeFortressUser(
                        emp.getEmail(),
                        emp.getHashPassword(),
                        Set.of("USER"), // Roles hardcodeados para el ejemplo
                        emp.isActivo()
                ));
    }


    @Override
    public CodeFortressUser save(CodeFortressUser user) {
        // Implementar lógica para guardar en 'vieja_empleados' si se desea registro
        return user;
    }
}