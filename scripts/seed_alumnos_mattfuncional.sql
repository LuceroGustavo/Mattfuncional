-- Seed de alumnos para pruebas (Mattfuncional)
-- Notas:
-- - tipo_asistencia (enum ordinal): PRESENCIAL=0, ONLINE=1
-- - dia (enum ordinal): LUNES=0, MARTES=1, MIERCOLES=2, JUEVES=3, VIERNES=4, SABADO=5, DOMINGO=6

SET @profesor_id := (SELECT id FROM profesor LIMIT 1);

-- 1) Ana Lopez - 2 veces por semana (Lun/Mie 18-19) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Ana', 'Lopez', 23, 'Femenino', 58.5, 'NO_LOGIN', 'ALUMNO', 'alumno01@mattfuncional.com', 0, '1122336601', 'ACTIVO', '2026-02-01', NULL, 'ALTA: 2026-02-01', '2026-02-01', 'ACTIVA', '2 veces/semana', @profesor_id);
SET @u1 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u1, 0, '18:00:00', '19:00:00'),
       (@u1, 2, '18:00:00', '19:00:00');

-- 2) Bruno Diaz - 2 veces por semana (Mar/Jue 19-20) - Online
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Bruno', 'Diaz', 29, 'Masculino', 82.3, 'NO_LOGIN', 'ALUMNO', 'alumno02@mattfuncional.com', 1, '1122336602', 'ACTIVO', '2026-02-02', NULL, 'ALTA: 2026-02-02', '2026-02-02', 'ACTIVA', '2 veces/semana', @profesor_id);
SET @u2 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u2, 1, '19:00:00', '20:00:00'),
       (@u2, 3, '19:00:00', '20:00:00');

-- 3) Carla Ruiz - 2 veces por semana (Lun/Vie 07-08) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Carla', 'Ruiz', 31, 'Femenino', 64.0, 'NO_LOGIN', 'ALUMNO', 'alumno03@mattfuncional.com', 0, '1122336603', 'ACTIVO', '2026-02-03', NULL, 'ALTA: 2026-02-03', '2026-02-03', 'ACTIVA', '2 veces/semana', @profesor_id);
SET @u3 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u3, 0, '07:00:00', '08:00:00'),
       (@u3, 4, '07:00:00', '08:00:00');

-- 4) Diego Gomez - 2 veces por semana (Mar/Jue 08-09) - Online
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Diego', 'Gomez', 27, 'Masculino', 76.2, 'NO_LOGIN', 'ALUMNO', 'alumno04@mattfuncional.com', 1, '1122336604', 'ACTIVO', '2026-02-04', NULL, 'ALTA: 2026-02-04', '2026-02-04', 'ACTIVA', '2 veces/semana', @profesor_id);
SET @u4 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u4, 1, '08:00:00', '09:00:00'),
       (@u4, 3, '08:00:00', '09:00:00');

-- 5) Elena Perez - 2 veces por semana (Mie/Sab 09-10) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Elena', 'Perez', 35, 'Femenino', 70.0, 'NO_LOGIN', 'ALUMNO', 'alumno05@mattfuncional.com', 0, '1122336605', 'ACTIVO', '2026-02-05', NULL, 'ALTA: 2026-02-05', '2026-02-05', 'ACTIVA', '2 veces/semana', @profesor_id);
SET @u5 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u5, 2, '09:00:00', '10:00:00'),
       (@u5, 5, '09:00:00', '10:00:00');

-- 6) Franco Silva - 2 veces por semana (Lun/Mie 20-21) - Online
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Franco', 'Silva', 22, 'Masculino', 68.4, 'NO_LOGIN', 'ALUMNO', 'alumno06@mattfuncional.com', 1, '1122336606', 'ACTIVO', '2026-02-06', NULL, 'ALTA: 2026-02-06', '2026-02-06', 'ACTIVA', '2 veces/semana', @profesor_id);
SET @u6 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u6, 0, '20:00:00', '21:00:00'),
       (@u6, 2, '20:00:00', '21:00:00');

-- 7) Gabriela Torres - 2 veces por semana (Mar/Jue 06-07) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Gabriela', 'Torres', 41, 'Femenino', 62.1, 'NO_LOGIN', 'ALUMNO', 'alumno07@mattfuncional.com', 0, '1122336607', 'ACTIVO', '2026-02-07', NULL, 'ALTA: 2026-02-07', '2026-02-07', 'ACTIVA', '2 veces/semana', @profesor_id);
SET @u7 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u7, 1, '06:00:00', '07:00:00'),
       (@u7, 3, '06:00:00', '07:00:00');

-- 8) Hugo Rojas - 2 veces por semana (Lun/Mie 12-13) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Hugo', 'Rojas', 33, 'Masculino', 90.5, 'NO_LOGIN', 'ALUMNO', 'alumno08@mattfuncional.com', 0, '1122336608', 'ACTIVO', '2026-02-08', NULL, 'ALTA: 2026-02-08', '2026-02-08', 'ACTIVA', '2 veces/semana', @profesor_id);
SET @u8 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u8, 0, '12:00:00', '13:00:00'),
       (@u8, 2, '12:00:00', '13:00:00');

-- 9) Ivana Castro - 2 veces por semana (Mar/Jue 13-14) - Online
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Ivana', 'Castro', 26, 'Femenino', 55.9, 'NO_LOGIN', 'ALUMNO', 'alumno09@mattfuncional.com', 1, '1122336609', 'ACTIVO', '2026-02-09', NULL, 'ALTA: 2026-02-09', '2026-02-09', 'ACTIVA', '2 veces/semana', @profesor_id);
SET @u9 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u9, 1, '13:00:00', '14:00:00'),
       (@u9, 3, '13:00:00', '14:00:00');

-- 10) Javier Mendez - 2 veces por semana (Mie/Vie 18-19) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Javier', 'Mendez', 38, 'Masculino', 85.0, 'NO_LOGIN', 'ALUMNO', 'alumno10@mattfuncional.com', 0, '1122336610', 'ACTIVO', '2026-02-10', NULL, 'ALTA: 2026-02-10', '2026-02-10', 'ACTIVA', '2 veces/semana', @profesor_id);
SET @u10 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u10, 2, '18:00:00', '19:00:00'),
       (@u10, 4, '18:00:00', '19:00:00');

-- 11) Karina Luna - 3 veces por semana (Lun/Mie/Vie 07-08) - Online
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Karina', 'Luna', 24, 'Femenino', 59.0, 'NO_LOGIN', 'ALUMNO', 'alumno11@mattfuncional.com', 1, '1122336611', 'ACTIVO', '2026-02-11', NULL, 'ALTA: 2026-02-11', '2026-02-11', 'ACTIVA', '3 veces/semana', @profesor_id);
SET @u11 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u11, 0, '07:00:00', '08:00:00'),
       (@u11, 2, '07:00:00', '08:00:00'),
       (@u11, 4, '07:00:00', '08:00:00');

-- 12) Lucas Vega - 3 veces por semana (Mar/Jue/Sab 18-19) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Lucas', 'Vega', 28, 'Masculino', 79.4, 'NO_LOGIN', 'ALUMNO', 'alumno12@mattfuncional.com', 0, '1122336612', 'ACTIVO', '2026-02-12', NULL, 'ALTA: 2026-02-12', '2026-02-12', 'ACTIVA', '3 veces/semana', @profesor_id);
SET @u12 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u12, 1, '18:00:00', '19:00:00'),
       (@u12, 3, '18:00:00', '19:00:00'),
       (@u12, 5, '18:00:00', '19:00:00');

-- 13) Martina Ibarra - 3 veces por semana (Lun/Mie/Vie 19-20) - Online
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Martina', 'Ibarra', 32, 'Femenino', 63.8, 'NO_LOGIN', 'ALUMNO', 'alumno13@mattfuncional.com', 1, '1122336613', 'ACTIVO', '2026-02-13', NULL, 'ALTA: 2026-02-13', '2026-02-13', 'ACTIVA', '3 veces/semana', @profesor_id);
SET @u13 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u13, 0, '19:00:00', '20:00:00'),
       (@u13, 2, '19:00:00', '20:00:00'),
       (@u13, 4, '19:00:00', '20:00:00');

-- 14) Nicolas Soto - 3 veces por semana (Lun/Mie/Vie 08-09) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Nicolas', 'Soto', 21, 'Masculino', 71.2, 'NO_LOGIN', 'ALUMNO', 'alumno14@mattfuncional.com', 0, '1122336614', 'ACTIVO', '2026-02-14', NULL, 'ALTA: 2026-02-14', '2026-02-14', 'ACTIVA', '3 veces/semana', @profesor_id);
SET @u14 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u14, 0, '08:00:00', '09:00:00'),
       (@u14, 2, '08:00:00', '09:00:00'),
       (@u14, 4, '08:00:00', '09:00:00');

-- 15) Olivia Flores - 3 veces por semana (Mar/Jue/Sab 09-10) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Olivia', 'Flores', 36, 'Femenino', 66.7, 'NO_LOGIN', 'ALUMNO', 'alumno15@mattfuncional.com', 0, '1122336615', 'ACTIVO', '2026-02-15', NULL, 'ALTA: 2026-02-15', '2026-02-15', 'ACTIVA', '3 veces/semana', @profesor_id);
SET @u15 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u15, 1, '09:00:00', '10:00:00'),
       (@u15, 3, '09:00:00', '10:00:00'),
       (@u15, 5, '09:00:00', '10:00:00');

-- 16) Pablo Acosta - 3 veces por semana (Lun/Mar/Jue 20-21) - Online
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Pablo', 'Acosta', 30, 'Masculino', 80.1, 'NO_LOGIN', 'ALUMNO', 'alumno16@mattfuncional.com', 1, '1122336616', 'ACTIVO', '2026-02-16', NULL, 'ALTA: 2026-02-16', '2026-02-16', 'ACTIVA', '3 veces/semana', @profesor_id);
SET @u16 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u16, 0, '20:00:00', '21:00:00'),
       (@u16, 1, '20:00:00', '21:00:00'),
       (@u16, 3, '20:00:00', '21:00:00');

-- 17) Romina Navarro - 3 veces por semana (Mar/Jue/Sab 07-08) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Romina', 'Navarro', 27, 'Femenino', 60.3, 'NO_LOGIN', 'ALUMNO', 'alumno17@mattfuncional.com', 0, '1122336617', 'ACTIVO', '2026-02-17', NULL, 'ALTA: 2026-02-17', '2026-02-17', 'ACTIVA', '3 veces/semana', @profesor_id);
SET @u17 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u17, 1, '07:00:00', '08:00:00'),
       (@u17, 3, '07:00:00', '08:00:00'),
       (@u17, 5, '07:00:00', '08:00:00');

-- 18) Sergio Cabrera - 3 veces por semana (Lun/Mie/Vie 06-07) - Online
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Sergio', 'Cabrera', 45, 'Masculino', 88.9, 'NO_LOGIN', 'ALUMNO', 'alumno18@mattfuncional.com', 1, '1122336618', 'ACTIVO', '2026-02-18', NULL, 'ALTA: 2026-02-18', '2026-02-18', 'ACTIVA', '3 veces/semana', @profesor_id);
SET @u18 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u18, 0, '06:00:00', '07:00:00'),
       (@u18, 2, '06:00:00', '07:00:00'),
       (@u18, 4, '06:00:00', '07:00:00');

-- 19) Tamara Molina - 3 veces por semana (Lun/Mar/Jue 10-11) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Tamara', 'Molina', 29, 'Femenino', 57.5, 'NO_LOGIN', 'ALUMNO', 'alumno19@mattfuncional.com', 0, '1122336619', 'ACTIVO', '2026-02-19', NULL, 'ALTA: 2026-02-19', '2026-02-19', 'ACTIVA', '3 veces/semana', @profesor_id);
SET @u19 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u19, 0, '10:00:00', '11:00:00'),
       (@u19, 1, '10:00:00', '11:00:00'),
       (@u19, 3, '10:00:00', '11:00:00');

-- 20) Ulises Reyes - 3 veces por semana (Mar/Jue/Sab 11-12) - Online
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Ulises', 'Reyes', 34, 'Masculino', 83.6, 'NO_LOGIN', 'ALUMNO', 'alumno20@mattfuncional.com', 1, '1122336620', 'ACTIVO', '2026-02-20', NULL, 'ALTA: 2026-02-20', '2026-02-20', 'ACTIVA', '3 veces/semana', @profesor_id);
SET @u20 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u20, 1, '11:00:00', '12:00:00'),
       (@u20, 3, '11:00:00', '12:00:00'),
       (@u20, 5, '11:00:00', '12:00:00');

-- 21) Valeria Ortiz - Lunes a Viernes (08-09) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Valeria', 'Ortiz', 25, 'Femenino', 60.8, 'NO_LOGIN', 'ALUMNO', 'alumno21@mattfuncional.com', 0, '1122336621', 'ACTIVO', '2026-02-21', NULL, 'ALTA: 2026-02-21', '2026-02-21', 'ACTIVA', 'Lunes a Viernes', @profesor_id);
SET @u21 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u21, 0, '08:00:00', '09:00:00'),
       (@u21, 1, '08:00:00', '09:00:00'),
       (@u21, 2, '08:00:00', '09:00:00'),
       (@u21, 3, '08:00:00', '09:00:00'),
       (@u21, 4, '08:00:00', '09:00:00');

-- 22) Walter Suarez - Lunes a Viernes (19-20) - Online
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Walter', 'Suarez', 39, 'Masculino', 87.3, 'NO_LOGIN', 'ALUMNO', 'alumno22@mattfuncional.com', 1, '1122336622', 'ACTIVO', '2026-02-22', NULL, 'ALTA: 2026-02-22', '2026-02-22', 'ACTIVA', 'Lunes a Viernes', @profesor_id);
SET @u22 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u22, 0, '19:00:00', '20:00:00'),
       (@u22, 1, '19:00:00', '20:00:00'),
       (@u22, 2, '19:00:00', '20:00:00'),
       (@u22, 3, '19:00:00', '20:00:00'),
       (@u22, 4, '19:00:00', '20:00:00');

-- 23) Ximena Rios - Lunes a Viernes (07-08) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Ximena', 'Rios', 28, 'Femenino', 56.4, 'NO_LOGIN', 'ALUMNO', 'alumno23@mattfuncional.com', 0, '1122336623', 'ACTIVO', '2026-02-23', NULL, 'ALTA: 2026-02-23', '2026-02-23', 'ACTIVA', 'Lunes a Viernes', @profesor_id);
SET @u23 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u23, 0, '07:00:00', '08:00:00'),
       (@u23, 1, '07:00:00', '08:00:00'),
       (@u23, 2, '07:00:00', '08:00:00'),
       (@u23, 3, '07:00:00', '08:00:00'),
       (@u23, 4, '07:00:00', '08:00:00');

-- 24) Yago Alvarez - Lunes a Viernes (18-19) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Yago', 'Alvarez', 31, 'Masculino', 78.8, 'NO_LOGIN', 'ALUMNO', 'alumno24@mattfuncional.com', 0, '1122336624', 'ACTIVO', '2026-02-24', NULL, 'ALTA: 2026-02-24', '2026-02-24', 'ACTIVA', 'Lunes a Viernes', @profesor_id);
SET @u24 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u24, 0, '18:00:00', '19:00:00'),
       (@u24, 1, '18:00:00', '19:00:00'),
       (@u24, 2, '18:00:00', '19:00:00'),
       (@u24, 3, '18:00:00', '19:00:00'),
       (@u24, 4, '18:00:00', '19:00:00');

-- 25) Zoe Benitez - Lunes a Viernes (12-13) - Online
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Zoe', 'Benitez', 22, 'Femenino', 52.6, 'NO_LOGIN', 'ALUMNO', 'alumno25@mattfuncional.com', 1, '1122336625', 'ACTIVO', '2026-02-25', NULL, 'ALTA: 2026-02-25', '2026-02-25', 'ACTIVA', 'Lunes a Viernes', @profesor_id);
SET @u25 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u25, 0, '12:00:00', '13:00:00'),
       (@u25, 1, '12:00:00', '13:00:00'),
       (@u25, 2, '12:00:00', '13:00:00'),
       (@u25, 3, '12:00:00', '13:00:00'),
       (@u25, 4, '12:00:00', '13:00:00');

-- 26) Andres Herrera - Lunes a Viernes (06-07) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Andres', 'Herrera', 40, 'Masculino', 92.1, 'NO_LOGIN', 'ALUMNO', 'alumno26@mattfuncional.com', 0, '1122336626', 'ACTIVO', '2026-02-26', NULL, 'ALTA: 2026-02-26', '2026-02-26', 'ACTIVA', 'Lunes a Viernes', @profesor_id);
SET @u26 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u26, 0, '06:00:00', '07:00:00'),
       (@u26, 1, '06:00:00', '07:00:00'),
       (@u26, 2, '06:00:00', '07:00:00'),
       (@u26, 3, '06:00:00', '07:00:00'),
       (@u26, 4, '06:00:00', '07:00:00');

-- 27) Belen Paredes - Lunes a Viernes (17-18) - Online
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Belen', 'Paredes', 33, 'Femenino', 61.2, 'NO_LOGIN', 'ALUMNO', 'alumno27@mattfuncional.com', 1, '1122336627', 'ACTIVO', '2026-02-27', NULL, 'ALTA: 2026-02-27', '2026-02-27', 'ACTIVA', 'Lunes a Viernes', @profesor_id);
SET @u27 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u27, 0, '17:00:00', '18:00:00'),
       (@u27, 1, '17:00:00', '18:00:00'),
       (@u27, 2, '17:00:00', '18:00:00'),
       (@u27, 3, '17:00:00', '18:00:00'),
       (@u27, 4, '17:00:00', '18:00:00');

-- 28) Carlos Quintero - Lunes a Viernes (14-15) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Carlos', 'Quintero', 37, 'Masculino', 84.4, 'NO_LOGIN', 'ALUMNO', 'alumno28@mattfuncional.com', 0, '1122336628', 'ACTIVO', '2026-02-28', NULL, 'ALTA: 2026-02-28', '2026-02-28', 'ACTIVA', 'Lunes a Viernes', @profesor_id);
SET @u28 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u28, 0, '14:00:00', '15:00:00'),
       (@u28, 1, '14:00:00', '15:00:00'),
       (@u28, 2, '14:00:00', '15:00:00'),
       (@u28, 3, '14:00:00', '15:00:00'),
       (@u28, 4, '14:00:00', '15:00:00');

-- 29) Daniela Salas - Lunes a Viernes (09-10) - Online (INACTIVA)
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Daniela', 'Salas', 27, 'Femenino', 58.9, 'NO_LOGIN', 'ALUMNO', 'alumno29@mattfuncional.com', 1, '1122336629', 'INACTIVO', '2025-11-01', '2026-01-15', 'ALTA: 2025-11-01 | BAJA: 2026-01-15', '2025-11-01', 'EN_PAUSA', 'Lunes a Viernes', @profesor_id);
SET @u29 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u29, 0, '09:00:00', '10:00:00'),
       (@u29, 1, '09:00:00', '10:00:00'),
       (@u29, 2, '09:00:00', '10:00:00'),
       (@u29, 3, '09:00:00', '10:00:00'),
       (@u29, 4, '09:00:00', '10:00:00');

-- 30) Esteban Vargas - Lunes a Viernes (20-21) - Presencial
INSERT INTO usuario (nombre, apellido, edad, sexo, peso, password, rol, correo, tipo_asistencia, celular, estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, estado_membresia, detalle_asistencia, profesor_id)
VALUES ('Esteban', 'Vargas', 35, 'Masculino', 86.0, 'NO_LOGIN', 'ALUMNO', 'alumno30@mattfuncional.com', 0, '1122336630', 'ACTIVO', '2026-03-01', NULL, 'ALTA: 2026-03-01', '2026-03-01', 'ACTIVA', 'Lunes a Viernes', @profesor_id);
SET @u30 := LAST_INSERT_ID();
INSERT INTO usuario_dias_horarios_asistencia (usuario_id, dia, hora_entrada, hora_salida)
VALUES (@u30, 0, '20:00:00', '21:00:00'),
       (@u30, 1, '20:00:00', '21:00:00'),
       (@u30, 2, '20:00:00', '21:00:00'),
       (@u30, 3, '20:00:00', '21:00:00'),
       (@u30, 4, '20:00:00', '21:00:00');
