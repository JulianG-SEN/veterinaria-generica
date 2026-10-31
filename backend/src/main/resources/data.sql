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
