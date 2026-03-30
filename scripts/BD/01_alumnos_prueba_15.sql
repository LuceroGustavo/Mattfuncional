-- =============================================================================
-- 15 alumnos de prueba para Mattfuncional
-- Ejecutar después de arrancar la app una vez (profesor + tablas).
-- Login: correos test_matt_pf_NN@mattfuncional.test — contraseña no definida
--       (NULL como en scripts de referencia); asignar desde el panel si hace falta.
-- =============================================================================

USE mattfuncional;

SET @profesor_id = COALESCE(
    (SELECT id FROM profesor WHERE correo = 'profesor@mattfuncional.com' LIMIT 1),
    (SELECT id FROM profesor ORDER BY id LIMIT 1)
);

INSERT INTO usuario (
    nombre, edad, sexo, peso, password, rol, avatar, correo,
    notas_profesor, objetivos_personales, restricciones_medicas, celular,
    estado_alumno, fecha_alta, fecha_baja, historial_estado, fecha_inicio, profesor_id
) VALUES
('María García López', 28, 'F', 65.5, NULL, 'ALUMNO', '/img/avatar1.png', 'test_matt_pf_01@mattfuncional.test',
 'Alumna muy comprometida. Preferir entrenar por la mañana.', 'Ganar masa muscular y definir abdomen.', 'Ninguna. Sin restricciones.',
 '+54 11 4567-8901', 'ACTIVO', CURDATE() - INTERVAL 180 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 180 DAY, @profesor_id),
('Carlos Rodríguez Pérez', 35, 'M', 82.0, NULL, 'ALUMNO', '/img/avatar2.png', 'test_matt_pf_02@mattfuncional.test',
 'Trabaja de noche, prefiere entrenar tarde.', 'Bajar peso y mejorar resistencia cardiovascular.', 'Problemas de rodilla izquierda. Evitar impacto.',
 '+54 11 5678-9012', 'ACTIVO', CURDATE() - INTERVAL 120 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 120 DAY, @profesor_id),
('Ana Martínez Fernández', 42, 'F', 58.3, NULL, 'ALUMNO', '/img/avatar3.png', 'test_matt_pf_03@mattfuncional.test',
 'Madre de dos hijos. Horario flexible.', 'Tonificar y mantener flexibilidad.', 'Hipertensión controlada con medicación.',
 '+54 11 6789-0123', 'ACTIVO', CURDATE() - INTERVAL 90 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 90 DAY, @profesor_id),
('Diego Sánchez Ruiz', 22, 'M', 75.0, NULL, 'ALUMNO', '/img/avatar4.png', 'test_matt_pf_04@mattfuncional.test',
 'Estudiante universitario. Entrena 4 veces por semana.', 'Aumentar fuerza y volumen.', 'Ninguna.', '+54 11 7890-1234',
 'ACTIVO', CURDATE() - INTERVAL 60 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 60 DAY, @profesor_id),
('Laura Torres González', 31, 'F', 62.0, NULL, 'ALUMNO', '/img/avatar5.png', 'test_matt_pf_05@mattfuncional.test',
 'Trabaja de oficina.', 'Perder 5 kg y mejorar postura.', 'Lumbalgia crónica. Evitar alto impacto.',
 '+54 11 8901-2345', 'ACTIVO', CURDATE() - INTERVAL 45 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 45 DAY, @profesor_id),
('Fernando López Díaz', 48, 'M', 88.5, NULL, 'ALUMNO', '/img/avatar6.png', 'test_matt_pf_06@mattfuncional.test',
 'Ejecutivo. Poco tiempo disponible.', 'Mantener salud cardiovascular.', 'Diabetes tipo 2.',
 '+54 11 9012-3456', 'ACTIVO', CURDATE() - INTERVAL 200 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 200 DAY, @profesor_id),
('Valentina Romero Castro', 19, 'F', 55.0, NULL, 'ALUMNO', '/img/avatar7.png', 'test_matt_pf_07@mattfuncional.test',
 'Deportista. Hace natación además.', 'Mejorar rendimiento y fuerza en tren superior.', 'Ninguna.', '+54 11 0123-4567',
 'ACTIVO', CURDATE() - INTERVAL 30 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 30 DAY, @profesor_id),
('Ricardo Morales Vega', 55, 'M', 90.0, NULL, 'ALUMNO', '/img/avatar8.png', 'test_matt_pf_08@mattfuncional.test',
 'Jubilado. Mucho tiempo libre.', 'Movilidad articular y socializar.', 'Artrosis de cadera. Evitar sentadillas profundas.',
 '+54 11 1234-5678', 'INACTIVO', CURDATE() - INTERVAL 250 DAY, CURDATE() - INTERVAL 30 DAY, 'ALTA|BAJA', CURDATE() - INTERVAL 250 DAY, @profesor_id),
('Sofía Herrera Mendoza', 26, 'F', 60.0, NULL, 'ALUMNO', '/img/avatar1.png', 'test_matt_pf_09@mattfuncional.test',
 'Empleada en gimnasio.', 'Definición muscular.', 'Alergia al polvo.', '+54 11 2345-6789',
 'ACTIVO', CURDATE() - INTERVAL 15 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 15 DAY, @profesor_id),
('Pablo Jiménez Silva', 38, 'M', 78.0, NULL, 'ALUMNO', '/img/avatar2.png', 'test_matt_pf_10@mattfuncional.test',
 'Entrenador personal.', 'Mantener fuerza y movilidad.', 'Ninguna.', '+54 11 3456-7890',
 'ACTIVO', CURDATE() - INTERVAL 100 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 100 DAY, @profesor_id),
('Lucía Navarro Vega', 29, 'F', 63.0, NULL, 'ALUMNO', '/img/avatar3.png', 'test_matt_pf_11@mattfuncional.test',
 'Prefiere entrenar con bandas.', 'Tonificar piernas y glúteos.', 'Ninguna.', '+54 11 4000-0001',
 'ACTIVO', CURDATE() - INTERVAL 70 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 70 DAY, @profesor_id),
('Martín Acosta Rey', 33, 'M', 79.0, NULL, 'ALUMNO', '/img/avatar4.png', 'test_matt_pf_12@mattfuncional.test',
 'Corre 10k los fines de semana.', 'Ganar resistencia sin perder masa.', 'Esguince de tobillo hace 1 año.', '+54 11 4000-0002',
 'ACTIVO', CURDATE() - INTERVAL 55 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 55 DAY, @profesor_id),
('Camila Ortega Ruiz', 24, 'F', 57.0, NULL, 'ALUMNO', '/img/avatar5.png', 'test_matt_pf_13@mattfuncional.test',
 'Estudiante de medicina.', 'Mantener actividad y bajar estrés.', 'Ninguna.', '+54 11 4000-0003',
 'ACTIVO', CURDATE() - INTERVAL 40 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 40 DAY, @profesor_id),
('Gustavo Paredes Luna', 41, 'M', 92.0, NULL, 'ALUMNO', '/img/avatar6.png', 'test_matt_pf_14@mattfuncional.test',
 'Sedentario reciente.', 'Bajar grasa visceral.', 'Colesterol alto — dieta en curso.', '+54 11 4000-0004',
 'ACTIVO', CURDATE() - INTERVAL 25 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 25 DAY, @profesor_id),
('Nadia Ferreira Costa', 27, 'F', 61.0, NULL, 'ALUMNO', '/img/avatar7.png', 'test_matt_pf_15@mattfuncional.test',
 'Crossfit ocasional.', 'Mejorar técnica en levantamientos.', 'Ninguna.', '+54 11 4000-0005',
 'ACTIVO', CURDATE() - INTERVAL 18 DAY, NULL, 'ALTA', CURDATE() - INTERVAL 18 DAY, @profesor_id);

SELECT CONCAT('Insertados ', COUNT(*), ' alumnos de prueba Matt PF.') AS resultado
FROM usuario WHERE correo LIKE 'test_matt_pf_%@mattfuncional.test';
