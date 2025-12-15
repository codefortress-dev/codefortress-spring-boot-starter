package dev.example.custom;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vieja_empleados")
@Data
public class EmpleadoLegacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email; // Lo usaremos como username
    private String hashPassword;
    private boolean activo;
    // Getters y Setters...
}