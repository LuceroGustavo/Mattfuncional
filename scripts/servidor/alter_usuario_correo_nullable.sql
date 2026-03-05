-- Permite que la columna correo sea NULL para alumnos (correo opcional).
-- Ejecutar contra la base de datos Mattfuncional si la columna fue creada como NOT NULL.

ALTER TABLE usuario MODIFY COLUMN correo VARCHAR(255) NULL;
