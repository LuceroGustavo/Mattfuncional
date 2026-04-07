# Plan de Validación - Fixes de Backup y Scripts (abril 2026)

**Objetivo:** Verificar que los 5 bugs encontrados han sido corregidos correctamente.

**Tiempo estimado:** 30 minutos  
**Requisitos:** MySQL CLI, Postman o navegador, acceso a localhost:8080

---

## ✅ TEST 1: Validación script 02 (ejercicios vacíos)

### Escenario
Script falla si `exercise` table está vacía.

### Pasos

1. **Preparar BD vacía:**
   ```bash
   mysql -u root -p -e "DROP DATABASE IF EXISTS mattfuncional; CREATE DATABASE mattfuncional;"
   ```

2. **Iniciar Spring SIN ejecutar scripts:**
   - Asegurar que `ddl-auto=update` en `application.properties`
   - Esperar a que DataInitializer cargue 60 ejercicios y 5 categorías
   - Verificar:
     ```sql
     SELECT COUNT(*) FROM exercise;  -- Debe ser 60
     SELECT COUNT(*) FROM categoria WHERE profesor_id IS NULL;  -- Debe ser 5
     ```

3. **Ahora ejecutar scripts en orden:**
   ```bash
   mysql -u root -p mattfuncional < scripts/BD/01_alumnos_prueba_15.sql
   mysql -u root -p mattfuncional < scripts/BD/02_series_prueba_15.sql
   ```

4. **Verificar resultado:**
   - Script 02 debe ejecutarse sin errores
   - Verificar:
     ```sql
     SELECT COUNT(*) FROM serie WHERE nombre LIKE 'Matt PF Serie %';  -- Debe ser 15
     SELECT COUNT(*) FROM serie_ejercicio;  -- Debe ser > 0 (cada serie tiene ejercicios)
     ```

5. **Test negativo - deberías ver el error:**
   ```bash
   # Limpiar todo incluyendo categorías
   mysql -u root -p mattfuncional < scripts/BD/00_limpiar_datos_prueba_matt.sql
   DELETE FROM categoria;  -- Borrar completamente
   DELETE FROM exercise;   -- Borrar completamente
   
   # Intenta ejecutar 02 de nuevo
   mysql -u root -p mattfuncional < scripts/BD/02_series_prueba_15.sql
   ```
   - Debe fallar con mensaje claro: `ERROR: La tabla exercise está vacía...`

### ✅ PASA SI:
- Script 02 ejecuta exitosamente cuando `exercise` tiene datos
- Script muestra error claro cuando `exercise` está vacía
- No hay `exercise_id = NULL` en `serie_ejercicio`

---

## ✅ TEST 2: Validación script 03 (categorías garantizadas)

### Escenario
Script crea categorías automáticamente si no existen.

### Pasos

1. **Desde estado limpio, ejecutar:**
   ```bash
   mysql -u root -p mattfuncional < scripts/BD/01_alumnos_prueba_15.sql
   mysql -u root -p mattfuncional < scripts/BD/02_series_prueba_15.sql
   ```

2. **Borrar categorías (simular eliminación accidental):**
   ```sql
   DELETE FROM rutina_categoria;
   DELETE FROM categoria WHERE profesor_id IS NULL;  -- Borrar sistema
   ```

3. **Ahora ejecutar script 03:**
   ```bash
   mysql -u root -p mattfuncional < scripts/BD/03_rutinas_prueba_10.sql
   ```

4. **Verificar:**
   - ✅ Script ejecuta exitosamente (NO falla con FK)
   - ✅ Categorías fueron recreadas:
     ```sql
     SELECT COUNT(*) FROM categoria WHERE profesor_id IS NULL;  -- Debe ser 5
     ```
   - ✅ Rutinas se vincularon a categorías:
     ```sql
     SELECT COUNT(*) FROM rutina_categoria;  -- Debe ser >0
     ```

### ✅ PASA SI:
- Script 03 NO falla aunque categorías fueron borradas
- Categorías del sistema se recrean automáticamente
- No hay `categoria_id = NULL` en `rutina_categoria`

---

## ✅ TEST 3: Validación script 00 (orden de borrado)

### Escenario
Script limpia datos en orden correcto sin violar FKs.

### Pasos

1. **Full setup with all data:**
   ```bash
   mysql -u root -p mattfuncional < scripts/BD/01_alumnos_prueba_15.sql
   mysql -u root -p mattfuncional < scripts/BD/02_series_prueba_15.sql
   mysql -u root -p mattfuncional < scripts/BD/03_rutinas_prueba_10.sql
   ```

2. **Ejecutar limpieza:**
   ```bash
   mysql -u root -p mattfuncional < scripts/BD/00_limpiar_datos_prueba_matt.sql
   ```

3. **Verificar resultado:**
   ```sql
   SELECT COUNT(*) FROM rutina WHERE nombre LIKE 'Matt PF Rutina %';  -- Debe ser 0
   SELECT COUNT(*) FROM serie WHERE nombre LIKE 'Matt PF Serie %';    -- Debe ser 0
   SELECT COUNT(*) FROM usuario WHERE correo LIKE 'test_matt_pf_%';   -- Debe ser 0
   SELECT COUNT(*) FROM serie_ejercicio;  -- Debe ser 0 (todo limpio)
   ```

4. **Ejercicios predeterminados deben permanecer:**
   ```sql
   SELECT COUNT(*) FROM exercise;  -- Debe ser 60 (Sin cambios)
   ```

### ✅ PASA SI:
- Script limpia exitosamente sin errores FK
- Todos los datos Matt PF son eliminados
- Datos predeterminados permanecen intactos

---

## ✅ TEST 4: Validación backup ZIP (Fix #4 - Categorías always imported)

### Escenario
Categorías se importan incluso si NO marcas ☑️ Rutinas o ☑️ Series

### Pasos

1. **Full setup:**
   ```bash
   mysql -u root -p mattfuncional < scripts/BD/01_alumnos_prueba_15.sql
   mysql -u root -p mattfuncional < scripts/BD/02_series_prueba_15.sql
   mysql -u root -p mattfuncional < scripts/BD/03_rutinas_prueba_10.sql
   ```

2. **Exportar backup completo via UI:**
   - Login: profesor@mattfuncional.com / profesor
   - Ir a: Administración → Backup y resguardo → Exportar
   - ☑️ Todos los checkboxes: Grupos, Ejercicios, Rutinas, Series
   - Descargar: `backup_YYYY-MM-DD_HH-MM-SS.zip`

3. **Crear categoría personalizada del profesor:**
   - Ir a: Panel → Mis Categorías
   - Nueva categoría: "Mi Categoría Test"
   - Asignar a una rutina existente

4. **Re-exportar ZIP:**
   - Mismo proceso → descargar nuevo ZIP
   - Verificar que `categorias.json` existe en ZIP y contiene "Mi Categoría Test"

5. **Simular import restringido:**
   - Limpiar: `00_limpiar_datos_prueba_matt.sql`
   - En panel: Importar ZIP  
   - ☑️ SOLO: Grupos, Ejercicios (SIN ☑️ Rutinas, SIN ☑️ Series)
   - Hacer import (Agregar)

6. **Verificar categorías importadas:**
   - Panels → Mis Categorías
   - ✅ "Mi Categoría Test" debe aparecer (incluso sin importar rutinas)
   - ✅ Las 5 categorías sistema deben estar

### ✅ PASA SI:
- "Mi Categoría Test" se importa aunque NO marcaste rutinas/series
- Categorización aparece en panel del profesor
- Sin errores en logs

---

## ✅ TEST 5: Validación backup ZIP (Fix #5 - ejercicioPorNombre consolidado)

### Escenario
Series se importan con ejercicios correctamente vinculados, incluso si NO marcas ☑️ Ejercicios

### Pasos

1. **Full setup:**
   ```bash
   mysql -u root -p mattfuncional < scripts/BD/01_alumnos_prueba_15.sql
   mysql -u root -p mattfuncional < scripts/BD/02_series_prueba_15.sql
   mysql -u root -p mattfuncional < scripts/BD/03_rutinas_prueba_10.sql
   ```

2. **Exportar backup:**
   - ☑️ Todos los checkboxes
   - Descargar ZIP

3. **Verificar estado antes de import:**
   ```sql
   SELECT COUNT(*) FROM serie_ejercicio WHERE exercise_id IS NOT NULL;  -- Anota el número, p.ej. N
   ```

4. **Simular import sin ejercicios:**
   - Ir a: Administración → Backup → Importar
   - Seleccionar ZIP
   - ☑️ SeriOS SOLO: Rutinas, Series (SIN ☑️ Ejercicios, SIN ☑️ Grupos)
   - Modo: Agregar
   - Importar

5. **Verificar resultado:**
   ```sql
   SELECT COUNT(*) FROM serie_ejercicio WHERE exercise_id IS NOT NULL;  -- Debe ser > N
   SELECT COUNT(*) FROM serie WHERE nombre LIKE 'Matt PF Serie %';
   SELECT se.*, e.name FROM serie_ejercicio se 
   LEFT JOIN exercise e ON e.id = se.exercise_id 
   WHERE se.ejercicio_id IS NULL;  -- Debe ser vacío (sin ejercicios huérfanos)
   ```

6. **En panel:**
   - Ir a Mis Series
   - Seleccionar cualquiera de las Matt PF Series importadas
   - Ir a "Ver Serie"
   - ✅ Debe mostrar lista de ejercicios (No "0 ejercicios")
   - ✅ Ejercicios deben ser los correctos (ejercicio 1, 2, 3, etc.)

### ✅ PASA SI:
- Series se importan con ejercicios correctamente vinculados
- "Ver Serie" muestra lista completa de ejercicios (no "0 ejercicios")
- No hay filas con `exercise_id = NULL` después del import

---

## 📊 Resumen de Validación

| Test | Bug Corregido | Estado | Observaciones |
|------|---------------|--------|---------------|
| TEST 1 | BUG #1 (script 02) | ❌ / ✅ | Si falla, el script no valida correctly |
| TEST 2 | BUG #3 (script 03) | ❌ / ✅ | Si falla, categorías no se crean |
| TEST 3 | BUG #2 (script 00) | ❌ / ✅ | Si falla, FK constraint errors |
| TEST 4 | BUG #4 (categorías import) | ❌ / ✅ | Si falla, categorías se pierden |
| TEST 5 | BUG #5 (ejercicioPorNombre) | ❌ / ✅ | Si falla, series sin ejercicios |

---

## 🔍 Comandos de Verificación Rápida

```sql
-- Verificar integridad de serie_ejercicio
SELECT COUNT(*) as filas_sin_ejercicio FROM serie_ejercicio WHERE exercise_id IS NULL;
-- Resultado esperado: 0

-- Verificar integridad de rutina_categoria
SELECT COUNT(*) as filas_sin_categoria FROM rutina_categoria WHERE categoria_id IS NULL;
-- Resultado esperado: 0

-- Contar datos Matt PF (antes de limpiar)
SELECT 
  (SELECT COUNT(*) FROM rutina WHERE nombre LIKE 'Matt PF Rutina %') as rutinas,
  (SELECT COUNT(*) FROM serie WHERE nombre LIKE 'Matt PF Serie %') as series,
  (SELECT COUNT(*) FROM usuario WHERE correo LIKE 'test_matt_pf_%') as alumnos;
-- Esperado: 10, 15, 15

-- Contar datos Matt PF (después de limpiar)
SELECT 
  (SELECT COUNT(*) FROM rutina WHERE nombre LIKE 'Matt PF Rutina %') as rutinas,
  (SELECT COUNT(*) FROM serie WHERE nombre LIKE 'Matt PF Serie %') as series,
  (SELECT COUNT(*) FROM usuario WHERE correo LIKE 'test_matt_pf_%') as alumnos;
-- Esperado: 0, 0, 0

-- Verificar que ejercicios predeterminados permanecen
SELECT COUNT(*) FROM exercise WHERE profesor_id IS NULL;
-- Esperado: 60

-- Verificar categorías sistema
SELECT COUNT(*) FROM categoria WHERE profesor_id IS NULL;
-- Esperado: 5
```

---

**Completado ✅ cuando:** Todos los tests pasan sin errores SQL ni FK violations.
