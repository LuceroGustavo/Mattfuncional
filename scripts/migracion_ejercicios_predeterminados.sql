-- =====================================================
-- SCRIPT DE MIGRACIÓN: Ejercicios Predeterminados
-- Fecha: 2025-01-27
-- Descripción: Convierte ejercicios del admin a predeterminados
-- =====================================================

-- IMPORTANTE: Hacer backup completo de la base de datos antes de ejecutar este script
-- BACKUP: mysqldump -u root -p datagym > backup_antes_migracion_$(date +%Y%m%d_%H%M%S).sql

-- PASO 1: Agregar columna esPredeterminado si no existe
-- (Hibernate lo hará automáticamente con ddl-auto=update, pero lo incluimos por seguridad)
ALTER TABLE exercise 
ADD COLUMN IF NOT EXISTS es_predeterminado BOOLEAN DEFAULT FALSE NOT NULL;

-- PASO 2: Obtener ID del profesor admin
SET @admin_profesor_id = (SELECT id FROM profesor WHERE correo = 'admin@migym.com' LIMIT 1);

-- PASO 3: Marcar ejercicios del admin como predeterminados
UPDATE exercise 
SET es_predeterminado = TRUE, profesor_id = NULL
WHERE profesor_id = @admin_profesor_id;

-- PASO 4: Verificar que la migración fue exitosa
SELECT 
    COUNT(*) as total_ejercicios,
    SUM(CASE WHEN es_predeterminado = TRUE THEN 1 ELSE 0 END) as ejercicios_predeterminados,
    SUM(CASE WHEN profesor_id IS NULL THEN 1 ELSE 0 END) as ejercicios_sin_profesor,
    SUM(CASE WHEN profesor_id IS NOT NULL THEN 1 ELSE 0 END) as ejercicios_con_profesor
FROM exercise;

-- PASO 5: Mostrar resumen de ejercicios por profesor
SELECT 
    COALESCE(p.nombre, 'PREDETERMINADO') as profesor,
    COUNT(*) as cantidad_ejercicios
FROM exercise e
LEFT JOIN profesor p ON e.profesor_id = p.id
GROUP BY p.id, p.nombre
ORDER BY cantidad_ejercicios DESC;

-- =====================================================
-- NOTAS IMPORTANTES:
-- 1. Este script NO elimina ejercicios duplicados de profesores
-- 2. Los ejercicios duplicados se pueden limpiar manualmente después
-- 3. Las referencias en serie_ejercicio se mantienen intactas
-- 4. Verificar integridad después de la migración
-- =====================================================

