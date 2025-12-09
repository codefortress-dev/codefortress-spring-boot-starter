package dev.codefortress.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cf_roles") // Prefijo cf_ para evitar colisiones con tablas del usuario
@Getter @Setter
public class SecurityRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // Ej: "ADMIN", "USER"
}