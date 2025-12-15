package dev.example.custom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "dev.codefortress.custom", // Tu App
        //"dev.example.custom"            // <--- AGREGA ESTO: Donde vive tu Adapter y Repo si esta en otro lado
})
@EnableJpaRepositories(basePackages = {
        "dev.codefortress.custom"         // <--- IMPORTANTE: Donde vive tu Repositorio
        //"dev.codefortress.custom.repository" en mi caso todo esta ne la raiz

})
@EntityScan(basePackages = {
        "dev.codefortress.custom"          // <--- IMPORTANTE: Donde vive tu Entidad (EmpleadoLegacy)
        //"dev.codefortress.custom.domain" En mi caso todo esta en la raiz
})
public class CustomApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomApplication.class, args);
    }
}