-- Permite que la columna email sea NULL para soportar consultas solo con teléfono.
-- Ejecutar contra la base de datos Mattfuncional.

ALTER TABLE consulta MODIFY COLUMN email VARCHAR(150) NULL;
