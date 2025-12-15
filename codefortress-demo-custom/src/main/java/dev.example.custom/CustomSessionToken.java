package dev.example.custom;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_session_tokens") // Nombre de tabla diferente al default
@Data // Lombok
public class CustomSessionToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tokenUuid;

    private String usuarioEmail; // Usamos email en vez de username

    private LocalDateTime fechaExpiracion;
}