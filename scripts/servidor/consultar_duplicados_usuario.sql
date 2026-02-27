-- Consulta: detectar correos duplicados en tabla usuario
-- Si el calendario o el login fallan con "Query did not return a unique result", puede haber varios usuarios con el mismo correo.
--
-- Uso (solo lectura): mysql -u mattfuncional_user -p mattfuncional < scripts/servidor/consultar_duplicados_usuario.sql

-- Listar correos que tienen más de un registro
SELECT correo, COUNT(*) AS cantidad
FROM usuario
GROUP BY correo
HAVING COUNT(*) > 1;

-- Opcional: listar todos los registros de esos correos (para decidir cuál conservar)
-- Descomentar si necesitás ver id, nombre, rol de cada duplicado:
/*
SELECT u.id, u.nombre, u.correo, u.rol, u.profesor_id
FROM usuario u
INNER JOIN (
  SELECT correo FROM usuario GROUP BY correo HAVING COUNT(*) > 1
) d ON u.correo = d.correo
ORDER BY u.correo, u.id;
*/
