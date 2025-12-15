package dev.codefortress.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity for a security role.
 */
@Entity
@Table(name = "cf_roles")
@Getter @Setter
public class SecurityRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}