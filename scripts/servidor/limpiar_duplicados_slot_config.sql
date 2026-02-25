-- Limpieza de duplicados en slot_config (si existen)
-- Ejecutar solo si el calendario falla con "Query did not return a unique result: 2 results were returned"
--
-- Uso: mysql -u mattfuncional_user -p mattfuncional < scripts/servidor/limpiar_duplicados_slot_config.sql

-- Eliminar duplicados dejando solo el de menor id por cada (dia, hora_inicio)
DELETE sc1 FROM slot_config sc1
INNER JOIN slot_config sc2
WHERE sc1.dia = sc2.dia
  AND sc1.hora_inicio = sc2.hora_inicio
  AND sc1.id > sc2.id;
