-- codefortress-schema.sql
-- Script de inicialización para CodeFortress Security
-- Ejecútalo si no usas hibernate.ddl-auto

-- 1. Tabla de Usuarios
CREATE TABLE IF NOT EXISTS cf_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- 2. Tabla de Roles
CREATE TABLE IF NOT EXISTS cf_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- 3. Tabla Intermedia (Many-to-Many)
CREATE TABLE IF NOT EXISTS cf_users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES cf_users(id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES cf_roles(id)
);

-- 4. Tabla de Refresh Tokens
CREATE TABLE IF NOT EXISTS cf_refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE, -- El token sí es único
    expiry_date TIMESTAMP NOT NULL,
    user_id BIGINT,
    -- SIN UNIQUE EN user_id
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES cf_users(id)
);