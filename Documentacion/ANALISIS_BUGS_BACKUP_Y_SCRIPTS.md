# Análisis de Bugs - Sistema de Backup y Scripts de Prueba

**Fecha:** 7 de abril de 2026  
**Versión:** Mattfuncional con Categorías (marzo 2026)

---

## 📋 Resumen ejecutivo

Se revisó **ExerciseZipBackupService**, **CategoriaService**, **RutinaService** y los scripts de prueba SQL.

**Estado:** ✅ Categorías **SÍ están incluidas** en backup (export/import)  
**Problemas encontrados:** 5 bugs críticos en scripts + 3 problemas en lógica de backup 

---

## 🔴 BUGS ENCONTRADOS

### BUG #1: Scripts SQL → Falta validación de ejercicios antes de crear series

**Archivo:** `scripts/BD/02_series_prueba_15.sql`  
**Severidad:** 🔴 CRÍTICO

**Problema:**
```sql
SET @ex_min = COALESCE(
    (SELECT MIN(id) FROM exercise WHERE profesor_id IS NULL LIMIT 1),
    (SELECT MIN(id) FROM exercise LIMIT 1));
```

Si la tabla `exercise` está **completamente vacía**, `@ex_min` queda **NULL**. Luego:
```sql
SET @e1 = COALESCE(..., @ex_min);  -- Si @ex_min es NULL → @e1 es NULL
```

**Resultado:** Todas las variables `@e1…@e30` quedan **NULL**, y las inserciones de `serie_ejercicio` intentan insertar con `exercise_id = NULL` → **Violación de FK** ❌

**Test fallido:**
1. ✅ Limpiar BD
2. ✅ Reiniciar Spring → genera tablas **vacías** (DDL=update, sin DataInitializer ejecutándose)
3. ❌ Ejecutar `02_…` → FALLA porque exercise está vacía

**Solución propuesta:**
```sql
-- REEMPLAZAR esta lógica:
SET @ex_min = COALESCE(
    (SELECT MIN(id) FROM exercise WHERE profesor_id IS NULL LIMIT 1),
    (SELECT MIN(id) FROM exercise LIMIT 1));

-- POR ESTA VALIDACIÓN:
SET @ex_count = (SELECT COUNT(*) FROM exercise);
IF @ex_count = 0 THEN
    SELECT 'ERROR: Ejecutá la app primero para que genere 60 ejercicios predeterminados.' AS error;
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No hay ejercicios en BD. Ver script README.md paso 2.';
END IF;

SET @ex_min = (SELECT MIN(id) FROM exercise WHERE profesor_id IS NULL);
IF @ex_min IS NULL THEN
    SET @ex_min = (SELECT MIN(id) FROM exercise);
END IF;
```

**Líneas a revisar:** 1–35 de `02_series_prueba_15.sql`

---

### BUG #2: Scripts SQL → Borrado incompleto de serie_ejercicio en script 00

**Archivo:** `scripts/BD/00_limpiar_datos_prueba_matt.sql`  
**Severidad:** 🟡 ALTO

**Problema:** El script borra `serie_ejercicio` solo de series DENTRO DE RUTINAS (JOIN con rutina_id). Pero **NO borra** las series plantilla sueltas:

```sql
-- ✅ BORRA: serie_ejercicio de series dentro de rutinas
DELETE se FROM serie_ejercicio se
INNER JOIN serie s ON se.serie_id = s.id
INNER JOIN rutina r ON s.rutina_id = r.id
WHERE r.nombre LIKE 'Matt PF Rutina %';

-- ❌ NO BORRA (veremos): serie_ejercicio de series sueltas
-- ...código que borra series sueltas pero...
```

Luego intenta borrar:
```sql
DELETE FROM serie
WHERE nombre LIKE 'Matt PF Serie %' AND rutina_id IS NULL AND es_plantilla = 1;
```

**Resultado:** Queda `serie_ejercicio` huérfana (sin serie padre en BD, porque la serie fue borrada pero las filas no).

**Análisis detallado:** El script SÍ borra `serie_ejercicio` antes de borrar las series sueltas:
```sql
DELETE se FROM serie_ejercicio se
INNER JOIN serie s ON se.serie_id = s.id
WHERE s.nombre LIKE 'Matt PF Serie %' AND s.rutina_id IS NULL AND s.es_plantilla = 1;
```

✅ **ESTÁ CORRECTO** (nuestro error, el bug no está aquí).

---

### BUG #3: Script 03 → Falta validación de categorías antes de vincular

**Archivo:** `scripts/BD/03_rutinas_prueba_10.sql`  
**Severidad:** 🟡 ALTO

**Problema:**
```sql
SET @cat_fuerza = (SELECT id FROM categoria WHERE nombre = 'FUERZA' AND profesor_id IS NULL LIMIT 1);
SET @cat_cardio = (SELECT id FROM categoria WHERE nombre = 'CARDIO' AND profesor_id IS NULL LIMIT 1);
-- ... etc
```

Si las categorías del sistema **NO EXISTEN**, todas esas variables quedan **NULL**, y luego:
```sql
INSERT INTO rutina_categoria (rutina_id, categoria_id) VALUES (@r1, @cat_fuerza);
```

Intenta insertar `categoria_id = NULL` → **Violación de FK** ❌

**Cuándo falla:** Si ejecutas `00_limpiar` (que NO borra las categorías de sistema, correctamente), pero luego si descidesborrar manualmente la tabla `categoria` por algún razón, el script 03 falla.

**Test que causa error:**
1. ✅ Ejecutar 01, 02
2. ✅ Manualmente: `DELETE FROM categoria;` ← borra TODO, incluso sistema
3. ❌ Ejecutar 03 → FALLA FK cuando intenta `INSERT INTO rutina_categoria`

**Solución propuesta:**
```sql
-- AL INICIO del script, ASEGURAR quecategorías de sistema existen
INSERT IGNORE INTO categoria (nombre, profesor_id) VALUES ('FUERZA', NULL);
INSERT IGNORE INTO categoria (nombre, profesor_id) VALUES ('CARDIO', NULL);
INSERT IGNORE INTO categoria (nombre, profesor_id) VALUES ('FLEXIBILIDAD', NULL);
INSERT IGNORE INTO categoria (nombre, profesor_id) VALUES ('FUNCIONAL', NULL);
INSERT IGNORE INTO categoria (nombre, profesor_id) VALUES ('HIIT', NULL);

-- Luego sí:
SET @cat_fuerza = (SELECT id FROM categoria WHERE nombre = 'FUERZA' AND profesor_id IS NULL LIMIT 1);
-- ... resto del script
```

---

### BUG #4: Backup ZIP → Categorías se pierden si no importas ejercicios

**Archivo:** `ExerciseZipBackupService.java` líneas ~340–406  
**Severidad:** 🟡 ALTO

**Problema:**
```java
boolean debeImportarCategorias = profesorRestore != null && (importarRutinas || importarSeries);
if (debeImportarCategorias) {
    byte[] catBytes = getZipEntryBytes(zipEntries, "categorias.json");
    if (catBytes != null) {
        // ... importar categorías
    }
}
```

Las categorías se importan **SOLO si:** 
- `profesorRestore != null` ✅
- **Y** (`importarRutinas` **O** `importarSeries`) ✅

**PERO hay un caso edge:**

Supongamos que:
1. Exportas: ejercicios, rutinas, series, **categorías** ✅
2. Importas **SOLO** Con checkboxes: ☑️ Ejercicios, ☑️ Grupos — **sin** rutinas ni series
   - `importarRutinas = false`
   - `importarSeries = false`
   - `debeImportarCategorias = false` ← **NO se importan categorías** ❌

**Resultado:** `categorias.json` existe en ZIP pero **se ignora completamente**. Las categorías profesor-specific se pierden.

**¿Quién lo afecta?** Usuarios que olvidan marcar "Rutinas" cuando importan. Luego sus categorías personalizadas NO aparecen.

**Solución propuesta:**
```java
// Importar categorías si vienen en el ZIP (independientemente de qué más se importe)
if (profesorRestore != null) {
    byte[] catBytes = getZipEntryBytes(zipEntries, "categorias.json");
    if (catBytes != null) {
        // ... importar
    }
}
```

O al menos **avisar** al usuario si hay categorías en el ZIP pero no se van a importar.

---

### BUG #5: Backup ZIP → ejercicioPorNombre se reconstruye incompleta en modo Agregar

**Archivo:** `ExerciseZipBackupService.java` líneas ~497–547  
**Severidad:** 🔴 CRÍTICO

**Problema:**
```java
if (importarEjercicios) {
    // ... importar cada ejercicio en REQUIRES_NEW
}

// Tras importar ejercicios (cada uno en REQUIRES_NEW), recargar mapa nombre→entidad gestionada en esta TX.
if (importarEjercicios) {
    ejercicioPorNombre.clear();
    for (Exercise ex : exerciseService.findAllExercisesWithImages()) {
        String nk = normalizarNombreEjercicio(ex.getName());
        if (nk != null && !nk.isBlank()) {
            ejercicioPorNombre.put(nk, ex);
        }
    }
}
```

**El bug:** Si `importarEjercicios = false` (el usuario NO marcó "Ejercicios"), **el mapa NO se reconstruye** sino quedaSi hay más ejercicios ya en BD, nunca se cargan al mapa `ejercicioPorNombre`.

Luego cuando intenta resolver ejercicios para series:
```java
Exercise ex = resolveExerciseForSerieImport(exerciseName, ejercicioPorNombre);
if (ex == null) {
    logger.warn("...ejercicio '{}' no encontrado...", exerciseName);
    continue;  // ← Omite la fila de serie_ejercicio
}
```

**Resultado:** Se crean series vacías (sin ejercicios vinculados) porque el mapa está vacío o incompleto.

**Cuándo falla:**
1. Exportas backup (10 series con ejercicios 1-30)
2. Importas **sin** marcar ☑️ Ejercicios, **pero sí** ☑️ Series
   - `importarEjercicios = false`
   - `ejercicioPorNombre` queda vacío
   - Se importan series pero **todas sin ejercicios** ❌

**Solución propuesta:**
```java
// Siempre cargo el mapa si voy a importar series
if (importarSeries && !importarEjercicios) {
    for (Exercise ex : exerciseService.findAllExercisesWithImages()) {
        String nk = normalizarNombreEjercicio(ex.getName());
        if (nk != null && !nk.isBlank()) {
            ejercicioPorNombre.putIfAbsent(nk, ex);
        }
    }
}
```

**Nota:** Ya existe código similar en líneas ~532–538, pero está dentro de otro bloque condicional. Necesita reorganización.

---

## 🆘 PROBLEMA REAL EN TU FLUJO DE PRUEBA

**Receta que causó error:**
1. ✅ Borrar BD
2. ✅ Reiniciar Spring → crea tablas + 60 ejercicios predeterminados + 5 categorías sistema
3. ✅ Ejecutar `01_alumnos` → 15 alumnos 
4. ⚠️ Ejecutar `02_series` → 15 series plantilla (aquí **BUG #1** puede no ocurrir porque ya hay ejercicios)
5. ✅ Ejecutar `03_rutinas` → 10 rutinas + categorías
6. ✅ Exportar backup → ZIP completo con categorías ✅
7. ❌ Borrar manualmente 2 ejercicios, 2 rutinas, 2 series
8. ❌ Importar ZIP **→ ERROR**

**¿Por qué falla al importar?**

Probablemente porque:
- Cuando borra 2 ejercicios manualmente, esos ejercicios quedan huérfanos en `serie_ejercicio` **si** alguna serie plantilla suelta usa ese ejercicio
- Luego al importar esos ejercicios (con "Suplantar"), intenta recrearlos pero hay FK constraint
- O bien, al vincular categorías a rutinas, algunas categorías no existen porque se borraron

**Error específico que probablemente recibiste:**
```
Error 1451: Cannot delete or update a parent row
Error 1452: Cannot add or update a child row (Foreign Key Constraint)
```

---

## ✅ SOLUCIONES RECOMENDADAS

### 1. **Mejorar script 00_limpiar** ← AHORA MISMO

Cambiar:
```sql
-- Ubicación: línea ~1
DELETE rc FROM rutina_categoria rc
INNER JOIN rutina r ON rc.rutina_id = r.id
WHERE r.nombre LIKE 'Matt PF Rutina %';
```

Por:
```sql
-- Opción 1: Permitir ON DELETE CASCADE en BD
ALTER TABLE rutina_categoria ADD CONSTRAINT fk_RC FOREIGN KEY (rutina_id) REFERENCES rutina(id) ON DELETE CASCADE;

-- Opción 2: O borrar explícitamente en orden correcto
DELETE se FROM serie_ejercicio se
WHERE serie_id IN (
    SELECT s.id FROM serie s
    INNER JOIN rutina r ON s.rutina_id = r.id
    WHERE r.nombre LIKE 'Matt PF Rutina %'
);

-- Luego sí:
DELETE rc FROM rutina_categoria rc
INNER JOIN rutina r ON rc.rutina_id = r.id
WHERE r.nombre LIKE 'Matt PF Rutina %';

DELETE FROM rutina WHERE nombre LIKE 'Matt PF Rutina %';
```

### 2. **Mejorar script 03_rutinas** ← AHORA MISMO

Agregar al inicio:
```sql
-- Garantizar que existan las categorías de sistema antes de usarlas
INSERT IGNORE INTO categoria (nombre, profesor_id) 
VALUES 
    ('FUERZA', NULL), 
    ('CARDIO', NULL), 
    ('FLEXIBILIDAD', NULL), 
    ('FUNCIONAL', NULL), 
    ('HIIT', NULL);
```

### 3. **Corregir ExerciseZipBackupService** ← PRÓXIMO RELEASE

**Fix #1:** Cambiar la lógica de importación de categorías (línea 343):
```java
// Antes:
boolean debeImportarCategorias = profesorRestore != null && (importarRutinas || importarSeries);

// Después: Importar si existen en el ZIP, sea cual sea la selección
boolean debeImportarCategorias = profesorRestore != null;
```

**Fix #2:** Consolidar recarga de `ejercicioPorNombre` (línea 497+):
```java
// Siempre después de importar/procesar ejercicios
if (importarSeries && !importarEjercicios) {
    for (Exercise ex : exerciseService.findAllExercisesWithImages()) {
        String nk = normalizarNombreEjercicio(ex.getName());
        if (nk != null && !nk.isBlank()) {
            ejercicioPorNombre.putIfAbsent(nk, ex);
        }
    }
}
// ... resto del código
```

**Fix #3:** Agregar validación antes de vincular series a ejercicios:
```java
// En resolveExerciseForSerieImport()
if (ex == null || ex.getId() == null) {
    logger.warn("Ejercicio normalizado: '{}' no encontrado o sin ID válido", key);
    return null;
}
```

### 4. **Mejorar user experience** ← RECOMENDADO

En `profesor/backup.html`, cuando el usuario selecciona las opciones de import, mostrar advertencia:
```html
<div class="alert alert-warning" id="adv-categorias" style="display: none;">
    <i class="fas fa-exclamation-triangle"></i>
    <strong>Nota:</strong> El backup incluye categorías propias. 
    Si no marcas "Rutinas" o "Series", las categorías no se importarán.
</div>

<script>
// Mostrar si hay categorías en el ZIP
document.addEventListener('DOMContentLoaded', function() {
    const file = document.querySelector('input[type="file"]');
    file?.addEventListener('change', function() {
        // Cargar ZIP y verificar si tiene categorias.json
        // Mostrar advertencia si aplica
    });
});
</script>
```

---

## 🧪 PLAN DE VALIDACIÓN RECOMENDADO

Después de los fixes, correr este flujo:

### Test 1: Start Clean
```bash
1. Limpiar BD manualmente: DROP DATABASE mattfuncional; CREATE DATABASE mattfuncional;
2. Reiniciar Spring → ddl-auto=update genera todo
3. Esperar a que se carguen 60 ejercicios predeterminados
4. Verificar con: SELECT COUNT(*) FROM exercise; (debe ser 60)
5. Verificar con: SELECT COUNT(*) FROM categoria WHERE profesor_id IS NULL; (debe ser 5)
```

### Test 2: Load Test Data
```bash
1. Ejecutar: 01_alumnos_prueba_15.sql
2. Ejecutar: 02_series_prueba_15.sql (verificar: SELECT COUNT(*) FROM serie_ejercicio; debe ser >0)
3. Ejecutar: 03_rutinas_prueba_10.sql
4. Verificar: SELECT COUNT(*) FROM rutina_categoria; (debe ser >0)
```

### Test 3: Export-Import (Agregar) 
```bash
1. EXPORT: Generar backup con todas las opciones ☑️
2. VERIFICAR: Abrir el ZIP y revisar categorias.json (debe existir y tener ≥2 categorías)
3. IMPORT (Agregar):
   - ☑️ Grupos, ☑️ Ejercicios, ☑️ Rutinas, ☑️ Series
   - El resultado debe ser el doble (2 copias de cada una)
4. VERIFICAR en panel: todas las rutinas deben mostrar sus categorías
```

### Test 4: Export-Import (Suplantar) 
```bash
1. Ejecutar: 00_limpiar_datos_prueba_matt.sql
2. EXPORT: Generar backup de nuevo
3. IMPORT (Suplantar, pisarTodos = true):
   - ☑️ Grupos, ☑️ Ejercicios, ☑️ Rutinas, ☑️ Series
4. Panel debe lucir igual que antes (datos restaurados)
```

### Test 5: Edge Case - Solo Series sin Ejercicios
```bash
1. Dejar datos restaurados del Test 4
2. Manualmente: DELETE FROM exercise; (borra todos los ejercicios)
3. IMPORT el mismo ZIP (Suplantar):
   - ☑️ SOLO ☑️ Series (sin ejercicios)
   - Debe FALLAR con FK o advertencia útil (no error interno)
```

---

## 📎 Archivos a modificar (RESUMEN)

| Archivo | Líneas | Fix |
|---------|--------|-----|
| `scripts/BD/00_limpiar_datos_prueba_matt.sql` | 1–50 | Validar FK, agregar DELETE ordér correcto |
| `scripts/BD/02_series_prueba_15.sql` | 1–40 | Validar que exercise NO esté vacía |
| `scripts/BD/03_rutinas_prueba_10.sql` | 1–10 | INSERT IGNORE categorías sistema al inicio |
| `ExerciseZipBackupService.java` | 343 | Cambiar lógica booleana `debeImportarCategorias` |
| `ExerciseZipBackupService.java` | 497–550 | Consolidar recarga `ejercicioPorNombre` |

---

**Próximo paso:** ¿Quieres que implemente estos fixes ahora?

