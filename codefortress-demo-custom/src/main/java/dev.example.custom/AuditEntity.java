package dev.example.custom;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "custom_audit_logs") // Nombre personalizado para tu tabla
@Data
public class AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username; // Quién (Principal)

    @Column(nullable = false)
    private String action;   // Qué hizo (LOGIN_SUCCESS, LOGIN_FAILURE, etc.)

    @Column(length = 1000)
    private String detail;   // Detalles del error o evento

    private LocalDateTime timestamp; // Cuándo ocurrió
}