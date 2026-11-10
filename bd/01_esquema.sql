-- =============================================================
-- Gestión de Actividades de Limpieza - Modelo de base de datos
-- Ejecutar en phpMyAdmin (XAMPP), pestaña SQL o Importar.
-- Se puede ejecutar varias veces sin borrar datos.
-- =============================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS gestion_limpieza
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gestion_limpieza;

-- Usuarios que pueden entrar al sistema
CREATE TABLE IF NOT EXISTS administrador (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    usuario         VARCHAR(50)  NOT NULL UNIQUE,
    contrasena_hash VARCHAR(100) NOT NULL,
    nombre          VARCHAR(100) NOT NULL
) ENGINE = InnoDB;

-- Jefes de cuadrilla
CREATE TABLE IF NOT EXISTS jefe_cuadrilla (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL
) ENGINE = InnoDB;

-- Cada cuadrilla tiene un solo jefe y un jefe dirige una sola cuadrilla
CREATE TABLE IF NOT EXISTS cuadrilla (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL UNIQUE,
    jefe_id INT          NOT NULL UNIQUE,
    CONSTRAINT fk_cuadrilla_jefe FOREIGN KEY (jefe_id)
        REFERENCES jefe_cuadrilla (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Personal a cargo del jefe (pertenece a una cuadrilla)
CREATE TABLE IF NOT EXISTS personal (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    apellidos    VARCHAR(100) NOT NULL,
    cuadrilla_id INT          NOT NULL,
    CONSTRAINT fk_personal_cuadrilla FOREIGN KEY (cuadrilla_id)
        REFERENCES cuadrilla (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Catálogo de colonias de Saltillo (SEPOMEX)
CREATE TABLE IF NOT EXISTS colonia (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    codigo_postal     CHAR(5)      NOT NULL,
    nombre            VARCHAR(150) NOT NULL,
    tipo_asentamiento VARCHAR(50)  NOT NULL,
    CONSTRAINT uq_colonia UNIQUE (codigo_postal, nombre, tipo_asentamiento)
) ENGINE = InnoDB;

-- Actividad de limpieza realizada por una cuadrilla en un lugar específico
CREATE TABLE IF NOT EXISTS actividad (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    detalle      TEXT         NOT NULL,
    fecha        DATE         NOT NULL,
    lugar        VARCHAR(200) NOT NULL,
    cuadrilla_id INT          NOT NULL,
    evidencia    LONGBLOB     NULL,
    CONSTRAINT fk_actividad_cuadrilla FOREIGN KEY (cuadrilla_id)
        REFERENCES cuadrilla (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Colonias atendidas en cada actividad (relación muchos a muchos)
CREATE TABLE IF NOT EXISTS actividad_colonia (
    actividad_id INT NOT NULL,
    colonia_id   INT NOT NULL,
    PRIMARY KEY (actividad_id, colonia_id),
    CONSTRAINT fk_ac_actividad FOREIGN KEY (actividad_id)
        REFERENCES actividad (id) ON DELETE CASCADE,
    CONSTRAINT fk_ac_colonia FOREIGN KEY (colonia_id)
        REFERENCES colonia (id) ON DELETE RESTRICT
) ENGINE = InnoDB;

-- Administrador inicial: usuario "admin", contraseña "admin123"
-- (la contraseña se guarda cifrada con SHA-256 y sal)
INSERT IGNORE INTO administrador (usuario, contrasena_hash, nombre)
VALUES ('admin', 'zL07oz3dy59rcPn4iuQAgQ==:9bnoWMhbaDDMxKBKdEtgpOKaaGLEXKKkYlARzJr16Zg=', 'Administrador');
