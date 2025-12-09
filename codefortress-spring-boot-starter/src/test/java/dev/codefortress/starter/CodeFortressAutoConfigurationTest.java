package dev.codefortress.starter;

import dev.codefortress.core.spi.CodeFortressUserProvider;
import dev.codefortress.jpa.adapter.JpaUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.assertj.core.api.Assertions.assertThat;



class CodeFortressAutoConfigurationTest {
    // Esta utilidad simula el arranque de una aplicación Web
    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    CodeFortressAutoConfiguration.class, // Tu config

                    // Configuración de Base de Datos (Ya la tenías)
                    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
                    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,

                    // --- AGREGAR ESTO ---
                    // Configuración base de Seguridad (Necesaria para crear el AuthenticationManager)
                    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                    org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
            ));

    @Test
    @DisplayName("Escenario A: Sin configuración de usuario -> Arranca JPA Adapter por defecto")
    void whenNoUserConfig_thenJpaAdapterIsActive() {
        contextRunner.run(context -> {
            // 1. Verificamos que el Bean del Core existe
            assertThat(context).hasSingleBean(CodeFortressUserProvider.class);

            // 2. Verificamos que la implementación concreta es la nuestra (JPA)
            assertThat(context).hasSingleBean(JpaUserProvider.class);

            // 3. Verificamos que NO es el bean custom
            assertThat(context).doesNotHaveBean("customUserProvider");

            System.out.println("TEST PASADO: JPA Adapter se activó correctamente.");
        });
    }

    @Test
    @DisplayName("Escenario B: Usuario define su propio Provider -> JPA Adapter se apaga")
    void whenUserDefinesProvider_thenJpaAdapterBacksOff() {
        contextRunner
                .withUserConfiguration(UserCustomConfig.class) // Simulamos que el usuario tiene su propia config
                .run(context -> {
                    // 1. Verificamos que hay un provider
                    assertThat(context).hasSingleBean(CodeFortressUserProvider.class);

                    // 2. IMPORTANTE: Verificamos que la implementación es la del usuario
                    assertThat(context.getBean(CodeFortressUserProvider.class))
                            .isInstanceOf(CustomUserProvider.class);

                    // 3. Verificamos que nuestro JpaUserProvider NO existe
                    // (Aquí está la prueba de fuego del @ConditionalOnMissingBean)
                    assertThat(context).doesNotHaveBean(JpaUserProvider.class);

                    System.out.println("✅ TEST PASADO: JPA Adapter se apagó al detectar configuración custom.");
                });
    }

    // --- Clases Mock para simular al usuario ---

    // 1. Una implementación falsa del usuario
    static class CustomUserProvider implements CodeFortressUserProvider {
        @Override
        public java.util.Optional<dev.codefortress.core.model.CodeFortressUser> findByUsername(String username) {
            return java.util.Optional.empty();
        }
        @Override
        public dev.codefortress.core.model.CodeFortressUser save(dev.codefortress.core.model.CodeFortressUser user) {
            return user;
        }
    }

    // 2. Una configuración falsa del usuario
    @Configuration
    static class UserCustomConfig {
        @Bean
        public CodeFortressUserProvider customUserProvider() {
            return new CustomUserProvider();
        }
    }
}