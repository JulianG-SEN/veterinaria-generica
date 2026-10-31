-- =====================================================================
--  VeterinariaGenerica.sql
--  Diseño de base de datos - Proyecto Veterinaria Genérica
--  Motor: MySQL 8 / MariaDB (compatible con H2 en modo MySQL)
--  Convención (igual que TiendaGenerica): tablas en minúscula,
--  columnas en camelCase, llaves foráneas con prefijo "id".
-- =====================================================================

DROP DATABASE IF EXISTS VeterinariaGenerica;
CREATE DATABASE VeterinariaGenerica CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE VeterinariaGenerica;

-- ---------------------------------------------------------------------
-- 1. Tablas de parámetros (catálogos)
-- ---------------------------------------------------------------------
CREATE TABLE tipodocumento (
  id   INT NOT NULL AUTO_INCREMENT,
  tipo VARCHAR(30) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UQ_TIPODOCUMENTO_TIPO (tipo)
);

CREATE TABLE rol (
  id     INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(30) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UQ_ROL_NOMBRE (nombre)
);

CREATE TABLE especie (
  id     INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UQ_ESPECIE_NOMBRE (nombre)
);

CREATE TABLE raza (
  id        INT NOT NULL AUTO_INCREMENT,
  idEspecie INT NOT NULL,
  nombre    VARCHAR(80) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UQ_RAZA_ESPECIE_NOMBRE (idEspecie, nombre),
  CONSTRAINT FK_ESPECIE_RAZA FOREIGN KEY (idEspecie) REFERENCES especie (id)
);

CREATE TABLE estadocita (
  id     INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(30) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UQ_ESTADOCITA_NOMBRE (nombre)
);

CREATE TABLE servicio (
  id          INT NOT NULL AUTO_INCREMENT,
  nombre      VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255) NULL,
  precio      DECIMAL(10,2) NOT NULL,
  duracionMin INT NOT NULL DEFAULT 30,
  activo      TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY UQ_SERVICIO_NOMBRE (nombre),
  CONSTRAINT CK_SERVICIO_PRECIO CHECK (precio >= 0),
  CONSTRAINT CK_SERVICIO_DURACION CHECK (duracionMin > 0)
);

CREATE TABLE vacuna (
  id          INT NOT NULL AUTO_INCREMENT,
  nombre      VARCHAR(100) NOT NULL,
  idEspecie   INT NOT NULL,
  descripcion VARCHAR(255) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UQ_VACUNA_NOMBRE_ESPECIE (nombre, idEspecie),
  CONSTRAINT FK_ESPECIE_VACUNA FOREIGN KEY (idEspecie) REFERENCES especie (id)
);

-- ---------------------------------------------------------------------
-- 2. Personas
-- ---------------------------------------------------------------------
-- Personal de la clínica que inicia sesión (administrador, veterinario, recepcionista)
CREATE TABLE usuario (
  id              INT NOT NULL AUTO_INCREMENT,
  idTipoDocumento INT NOT NULL,
  numeroDocumento VARCHAR(30) NOT NULL,
  nombre          VARCHAR(200) NOT NULL,
  nombreUsuario   VARCHAR(50) NOT NULL,
  password        VARCHAR(255) NOT NULL,   -- guardar siempre cifrada (BCrypt)
  email           VARCHAR(100) NOT NULL,
  idRol           INT NOT NULL,
  activo          TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY UQ_USUARIO_DOCUMENTO (idTipoDocumento, numeroDocumento),
  UNIQUE KEY UQ_USUARIO_NOMBREUSUARIO (nombreUsuario),
  UNIQUE KEY UQ_USUARIO_EMAIL (email),
  CONSTRAINT FK_TIPODOCUMENTO_USUARIO FOREIGN KEY (idTipoDocumento) REFERENCES tipodocumento (id),
  CONSTRAINT FK_ROL_USUARIO FOREIGN KEY (idRol) REFERENCES rol (id)
);

-- Dueños de las mascotas (misma estructura que cliente en TiendaGenerica)
CREATE TABLE cliente (
  id              INT NOT NULL AUTO_INCREMENT,
  idTipoDocumento INT NOT NULL,
  numeroDocumento VARCHAR(30) NOT NULL,
  nombre          VARCHAR(200) NOT NULL,
  direccion       VARCHAR(100) NOT NULL,
  email           VARCHAR(100) NOT NULL,
  telefono        VARCHAR(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UQ_CLIENTE_DOCUMENTO (idTipoDocumento, numeroDocumento),
  UNIQUE KEY UQ_CLIENTE_EMAIL (email),
  CONSTRAINT FK_TIPODOCUMENTO_CLIENTE FOREIGN KEY (idTipoDocumento) REFERENCES tipodocumento (id)
);

-- ---------------------------------------------------------------------
-- 3. Pacientes y atención
-- ---------------------------------------------------------------------
CREATE TABLE mascota (
  id              INT NOT NULL AUTO_INCREMENT,
  idCliente       INT NOT NULL,
  idRaza          INT NOT NULL,
  nombre          VARCHAR(80) NOT NULL,
  sexo            CHAR(1) NOT NULL,          -- M = macho, H = hembra
  fechaNacimiento DATE NULL,
  pesoKg          DECIMAL(6,2) NULL,
  color           VARCHAR(50) NULL,
  activo          TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  CONSTRAINT CK_MASCOTA_SEXO CHECK (sexo IN ('M','H')),
  CONSTRAINT CK_MASCOTA_PESO CHECK (pesoKg IS NULL OR pesoKg > 0),
  CONSTRAINT FK_CLIENTE_MASCOTA FOREIGN KEY (idCliente) REFERENCES cliente (id),
  CONSTRAINT FK_RAZA_MASCOTA FOREIGN KEY (idRaza) REFERENCES raza (id)
);

CREATE TABLE cita (
  id            INT NOT NULL AUTO_INCREMENT,
  idMascota     INT NOT NULL,
  idVeterinario INT NOT NULL,                -- usuario con rol Veterinario
  idServicio    INT NOT NULL,
  idEstadoCita  INT NOT NULL,
  fechaHora     DATETIME NOT NULL,
  motivo        VARCHAR(255) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UQ_CITA_VETERINARIO_FECHA (idVeterinario, fechaHora),  -- evita doble agenda
  CONSTRAINT FK_MASCOTA_CITA FOREIGN KEY (idMascota) REFERENCES mascota (id),
  CONSTRAINT FK_USUARIO_CITA FOREIGN KEY (idVeterinario) REFERENCES usuario (id),
  CONSTRAINT FK_SERVICIO_CITA FOREIGN KEY (idServicio) REFERENCES servicio (id),
  CONSTRAINT FK_ESTADOCITA_CITA FOREIGN KEY (idEstadoCita) REFERENCES estadocita (id)
);

CREATE TABLE historiaclinica (
  id            INT NOT NULL AUTO_INCREMENT,
  idMascota     INT NOT NULL,
  idCita        INT NULL,                    -- puede registrarse sin cita previa (urgencia)
  idVeterinario INT NOT NULL,
  fecha         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  diagnostico   VARCHAR(500) NOT NULL,
  tratamiento   VARCHAR(500) NULL,
  observaciones VARCHAR(500) NULL,
  PRIMARY KEY (id),
  CONSTRAINT FK_MASCOTA_HISTORIA FOREIGN KEY (idMascota) REFERENCES mascota (id),
  CONSTRAINT FK_CITA_HISTORIA FOREIGN KEY (idCita) REFERENCES cita (id),
  CONSTRAINT FK_USUARIO_HISTORIA FOREIGN KEY (idVeterinario) REFERENCES usuario (id)
);

CREATE TABLE vacunacion (
  id              INT NOT NULL AUTO_INCREMENT,
  idMascota       INT NOT NULL,
  idVacuna        INT NOT NULL,
  idVeterinario   INT NOT NULL,
  fechaAplicacion DATE NOT NULL,
  proximaDosis    DATE NULL,
  PRIMARY KEY (id),
  CONSTRAINT FK_MASCOTA_VACUNACION FOREIGN KEY (idMascota) REFERENCES mascota (id),
  CONSTRAINT FK_VACUNA_VACUNACION FOREIGN KEY (idVacuna) REFERENCES vacuna (id),
  CONSTRAINT FK_USUARIO_VACUNACION FOREIGN KEY (idVeterinario) REFERENCES usuario (id)
);

--

-- ---------------------------------------------------------------------
-- 4. Datos iniciales (los mismos que carga el backend en H2 desde data.sql)
-- ---------------------------------------------------------------------
-- =====================================================================
--  Datos iniciales de la Veterinaria Genérica
--  Usuarios de prueba:  admin / admin123   lprada / vet12345
--                       cvega / vet12345   mrey / recep123
-- =====================================================================
INSERT INTO tipodocumento (tipo) VALUES ('CEDULA'), ('TARJETA'), ('PASAPORTE');
INSERT INTO rol (nombre) VALUES ('ADMINISTRADOR'), ('VETERINARIO'), ('RECEPCIONISTA');
INSERT INTO estadocita (nombre) VALUES ('PROGRAMADA'), ('CONFIRMADA'), ('ATENDIDA'), ('CANCELADA');
INSERT INTO especie (nombre) VALUES ('Perro'), ('Gato'), ('Ave'), ('Conejo');
INSERT INTO raza (idEspecie, nombre) VALUES
  (1, 'Criollo'), (1, 'Labrador'), (1, 'Pastor Alemán'),
  (2, 'Criollo'), (2, 'Siamés'),
  (3, 'Canario'), (4, 'Cabeza de León');
INSERT INTO servicio (nombre, descripcion, precio, duracionMin, activo) VALUES
  ('Consulta general', 'Revisión clínica general', 50000, 30, TRUE),
  ('Vacunación', 'Aplicación de vacunas', 40000, 15, TRUE),
  ('Desparasitación', 'Tratamiento antiparasitario', 30000, 15, TRUE),
  ('Baño y peluquería', 'Baño, corte y limpieza', 45000, 60, TRUE),
  ('Cirugía menor', 'Procedimientos quirúrgicos menores', 250000, 90, TRUE);
INSERT INTO vacuna (nombre, idEspecie, descripcion) VALUES
  ('Parvovirus', 1, 'Refuerzo anual'),
  ('Rabia', 1, 'Refuerzo anual'),
  ('Triple felina', 2, 'Refuerzo anual'),
  ('Rabia', 2, 'Refuerzo anual');

INSERT INTO usuario (idTipoDocumento, numeroDocumento, nombre, nombreUsuario, password, email, idRol, activo) VALUES
  (1, '1001001001', 'Ana Torres',    'admin',  '$2b$10$ik.igSmyKCPkZiqDS2xVJ.GzDD5acinV62.iaIQv96PwwvDkEQS56', 'admin@veterinaria.com',          1, TRUE),
  (1, '1001001002', 'Luis Prada',    'lprada', '$2b$10$r6XorpFRBdA0Xdiqww930OFASMNA0JK36U3bbwQ7RB0B8eqXrAxny', 'luis.prada@veterinaria.com',    2, TRUE),
  (1, '1001001003', 'Marta Rey',     'mrey',   '$2b$10$5ic3qUM9dxeIHBztcee0iew2qXP5DHBqZ5ykFFYQTpBDCyEBeOSxq', 'marta.rey@veterinaria.com',     3, TRUE),
  (1, '1001001004', 'Carolina Vega', 'cvega',  '$2b$10$r6XorpFRBdA0Xdiqww930OFASMNA0JK36U3bbwQ7RB0B8eqXrAxny', 'carolina.vega@veterinaria.com', 2, TRUE);

INSERT INTO cliente (idTipoDocumento, numeroDocumento, nombre, direccion, email, telefono) VALUES
  (1, '1098765432', 'Carlos Eduardo Mendoza', 'Calle 45 # 28 - 10',   'carlos.mendoza@email.com', '3151234567'),
  (1, '63987654',   'María Paula Gómez',      'Carrera 15 # 33 - 42', 'maria.gomez@email.com',    '3109876543'),
  (2, '1095123456', 'Juan David Pérez',       'Avenida 27 # 14 - 05', 'juan.perez@email.com',     '3204567890');

INSERT INTO mascota (idCliente, idRaza, nombre, sexo, fechaNacimiento, pesoKg, color, activo) VALUES
  (1, 2, 'Rocky',  'M', '2021-03-10', 28.50, 'Dorado', TRUE),
  (2, 5, 'Luna',   'H', '2022-07-21',  4.20, 'Crema',  TRUE),
  (3, 1, 'Max',    'M', '2023-01-15', 12.00, 'Café',   TRUE),
  (1, 4, 'Pelusa', 'H', '2020-11-02',  3.80, 'Gris',   TRUE);

INSERT INTO cita (idMascota, idVeterinario, idServicio, idEstadoCita, fechaHora, motivo) VALUES
  (1, 2, 1, 1, '2026-11-03 09:00:00', 'Control anual'),
  (2, 2, 2, 2, '2026-11-03 10:00:00', 'Refuerzo de vacuna'),
  (3, 4, 1, 1, '2026-11-04 11:00:00', 'Revisión de la piel'),
  (4, 4, 3, 1, '2026-11-05 15:30:00', 'Desparasitación'),
  (1, 2, 2, 3, '2026-10-20 09:00:00', 'Vacuna anual');

INSERT INTO historiaclinica (idMascota, idCita, idVeterinario, fecha, diagnostico, tratamiento, observaciones) VALUES
  (1, 5,    2, '2026-10-20 09:30:00', 'Paciente sano, peso estable', 'Se aplica vacuna anual', 'Próximo control en 12 meses'),
  (2, NULL, 4, '2026-09-15 16:00:00', 'Otitis leve en oído izquierdo', 'Limpieza y gotas óticas por 7 días', 'Control en 10 días');

INSERT INTO vacunacion (idMascota, idVacuna, idVeterinario, fechaAplicacion, proximaDosis) VALUES
  (1, 1, 2, '2026-10-20', '2027-10-20'),
  (2, 3, 4, '2026-08-10', '2027-08-10');
