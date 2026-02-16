# Plan: Grupos musculares como entidad (profesor puede crear grupos)

**Estado:** **IMPLEMENTADO** (Febrero 2026)

**Objetivo:** Permitir que el profesor cree grupos musculares propios además de los del sistema, y usarlos al categorizar ejercicios, filtrar listas y filtrar series.

**Estado anterior (ya migrado):** El grupo muscular era un **enum** `MuscleGroup` (BRAZOS, PIERNAS, PECHO, ESPALDA, CARDIO, ELONGACION). Se sustituyó por la entidad `GrupoMuscular` y se eliminó el enum.

---

## 1. Uso actual del grupo muscular en la app

| Dónde | Uso |
|-------|-----|
| **Exercise (entidad)** | `@ElementCollection private Set<MuscleGroup> muscleGroups` → tabla de colección (exercise_muscle_groups o similar). |
| **ExerciseRepository** | `findByMuscleGroups(MuscleGroup muscleGroup)` para filtrar ejercicios por grupo. |
| **ExerciseService** | `modifyExercise(..., Set<MuscleGroup>)`, `findExercisesByMuscleGroup(MuscleGroup)`. |
| **ExerciseCargaDefaultOptimizado** | Asigna uno o más enum a cada uno de los 60 ejercicios predeterminados. |
| **ProfesorController (mis-ejercicios)** | Lista ejercicios; pasa `MuscleGroup.values()` al modelo; filtro por grupo (en front); al crear/editar ejercicio recibe `List<String>` y hace `MuscleGroup.valueOf(s)`. |
| **SerieController (crear serie)** | Parámetro `muscleGroup` opcional; filtra ejercicios con `e.getMuscleGroups().contains(muscleGroup)`. |
| **ExerciseController** | Varios endpoints que usan `MuscleGroup.values()` y `valueOf`. |
| **Templates** | Checkboxes o select con `muscleGroups` (valores del enum); badges con nombre del grupo; filtro por grupo. |
| **Export/Import ejercicios** | JSON con `muscleGroups` como array de strings (nombres del enum). |

Conclusión: el enum está muy integrado (entidad, repositorio, servicios, controladores, carga de predeterminados, export/import). La opción más limpia es **sustituir el enum por una entidad** y migrar todo.

---

## 2. Estrategia recomendada: entidad `GrupoMuscular`

### 2.1 Por qué cambiar a entidad

- El profesor puede **crear, editar y listar** sus propios grupos (por ejemplo "Fascia", "Core", "Mobilidad").
- Los grupos del sistema (los 6 actuales) se representan como **registros con `profesor_id = null`**.
- Un solo modelo: "grupo muscular" = registro en BD (sistema o del profesor). Filtros, ejercicios y series trabajan siempre con la misma entidad.
- Escalable: no hay que tocar código cada vez que se quiera un grupo nuevo del sistema; se puede añadir por datos o por un futuro panel de administración.

### 2.2 Alternativas descartadas

- **Mantener enum y añadir “grupos personalizados” en otra tabla:** Habría dos orígenes de grupos (enum + tabla). En ejercicios habría que combinar ambos, y en filtros/UI se complica. No recomendable.
- **Solo agregar más valores al enum:** No permite que el profesor cree grupos; cada nuevo grupo exigiría despliegue y cambio de código.

---

## 3. Diseño de la entidad `GrupoMuscular`

- **Tabla:** `grupo_muscular` (o `grupo_muscular`).
- **Campos:**
  - `id` (PK, autogenerado).
  - `nombre` (String, único por profesor: no dos grupos con el mismo nombre para el mismo profesor; los del sistema tienen `profesor_id` null y nombres únicos globales).
  - `profesor_id` (FK a profesor, **nullable**). `null` = grupo del sistema; no null = grupo creado por ese profesor.
- **Restricción única:** `(nombre, profesor_id)` para evitar duplicados (incluyendo sistema con profesor_id null).
- **Relación con Exercise:** `@ManyToMany` entre `Exercise` y `GrupoMuscular` con tabla intermedia `exercise_grupos` (exercise_id, grupo_muscular_id). Sustituye a la actual `@ElementCollection` de `MuscleGroup`.

---

## 4. Grupos del sistema (los 6 actuales)

- En **DataInitializer** (o equivalente al arranque): si no existen, insertar los 6 grupos con `nombre` = BRAZOS, PIERNAS, PECHO, ESPALDA, CARDIO, ELONGACION y `profesor_id = null`.
- Así los ejercicios predeterminados y cualquier ejercicio pueden seguir asignándose a “BRAZOS”, “PIERNAS”, etc., pero ahora son filas de `GrupoMuscular`.

---

## 5. Base de datos: borrar y recrear

- La estructura actual de `Exercise` usa una tabla de colección para un **enum** (columnas típicas: exercise_id + valor enum). Al pasar a `@ManyToMany` con `GrupoMuscular`, el esquema cambia.
- **Recomendación:** Dado que el proyecto es de prueba, **borrar la base y dejar que JPA cree el esquema desde cero** (con `ddl-auto=create` o `update` según convenga en dev). No hace falta migración SQL de datos viejos.
- Si en el futuro se necesitara conservar datos, se podría escribir un script de migración (leer ejercicios con sus enums, resolver GrupoMuscular por nombre, insertar en `exercise_grupos`).

---

## 6. Pasos de implementación (resumen)

1. **Crear entidad y repositorio**
   - `GrupoMuscular` (id, nombre, profesor_id con unique (nombre, profesor_id)).
   - `GrupoMuscularRepository` (findByProfesorId, findByProfesorIdIsNull, findByNombreAndProfesorId, etc.).

2. **Inicialización de datos**
   - En DataInitializer (o `ApplicationRunner`): asegurar que existan los 6 grupos del sistema (profesor_id null).

3. **Modificar Exercise**
   - Quitar `@ElementCollection Set<MuscleGroup> muscleGroups`.
   - Añadir `@ManyToMany` a `GrupoMuscular` (tabla `exercise_grupos`). Actualizar getters/setters y cualquier código que use `muscleGroups`.

4. **Servicios**
   - `GrupoMuscularService`: listar grupos para un profesor (sistema + del profesor), crear, editar, borrar (solo los del profesor), obtener por id.
   - `ExerciseService`: en crear/editar ejercicio trabajar con `Set<GrupoMuscular>` o IDs; actualizar `findExercisesByMuscleGroup` a `findByGruposContaining(GrupoMuscular)` o por id de grupo.
   - `ExerciseCargaDefaultOptimizado`: al crear los 60 predeterminados, resolver `GrupoMuscular` por nombre (BRAZOS, PIERNAS, etc.) y asignar esos entidades en lugar del enum.

5. **Controladores**
   - **ABM grupos musculares:** Rutas bajo profesor (ej. `/profesor/grupos-musculares`, listar; crear; editar; borrar). Solo se permiten editar/borrar grupos con `profesor_id` no null.
   - **ProfesorController (mis-ejercicios):** Obtener lista de grupos = sistema + del profesor; pasar al modelo en lugar de `MuscleGroup.values()`. En POST crear/editar ejercicio recibir IDs de grupos (o nombres) y resolver a `Set<GrupoMuscular>`.
   - **SerieController:** Filtro por grupo por id de `GrupoMuscular`; listar ejercicios que contengan ese grupo.
   - **ExerciseController:** Misma idea: reemplazar uso del enum por `GrupoMuscular` y lista de grupos desde servicio.

6. **Repositorio Exercise**
   - Reemplazar `findByMuscleGroups(MuscleGroup)` por consulta que use la relación con `GrupoMuscular` (por id o por entidad).

7. **Templates**
   - Donde hoy se usa `muscleGroups` (enum): listar grupos desde el modelo (lista de `GrupoMuscular`). Checkboxes/select por id (o nombre). Badges y filtros por nombre del grupo (grupo.getNombre()).
   - Añadir en el panel del profesor una sección o pestaña “Grupos musculares” (listar, alta, edición, baja de los propios).

8. **Export/Import**
   - Exportar ejercicios: en lugar de nombres de enum, exportar nombres de grupo (grupo.getNombre()). Importar: resolver por nombre (buscar GrupoMuscular del sistema o del profesor); si no existe, opcionalmente crear grupo del profesor o rechazar.

9. **Eliminar enum**
   - Una vez migrado todo, eliminar el enum `MuscleGroup` y sus referencias.

---

## 7. Archivos a tocar (lista orientativa)

- **Nuevos:** `GrupoMuscular.java`, `GrupoMuscularRepository.java`, `GrupoMuscularService.java`, vistas para listar/crear/editar grupos (ej. bajo `profesor/`).
- **Modificar:** `Exercise.java`, `ExerciseRepository.java`, `ExerciseService.java`, `ExerciseCargaDefaultOptimizado.java`, `ProfesorController.java`, `SerieController.java`, `ExerciseController.java`, `ExerciseExportImportService.java`, todos los templates que usen `muscleGroups` o filtro por grupo.
- **Eliminar (al final):** `MuscleGroup.java` (enum).

---

## 8. Aviso para el cliente / equipo

- **Es necesario borrar la base de datos** (o hacer migración explícita si más adelante se exige conservar datos). En entorno de pruebas está asumido.
- Tras el cambio, los 6 grupos actuales siguen existiendo como “grupos del sistema”; el profesor verá esos más los que cree él. Los ejercicios predeterminados se asignan a los grupos del sistema por nombre (BRAZOS, PIERNAS, etc.) en la carga inicial.

---

## 9. Implementación realizada (Febrero 2026)

### 9.1 Resumen

- **Entidad** `GrupoMuscular` (id, nombre, profesor_id nullable). Restricción única `(nombre, profesor_id)`.
- **Tabla asociativa** `exercise_grupos` (exercise_id, grupo_muscular_id). Exercise pasa de `@ElementCollection Set<MuscleGroup>` a `@ManyToMany Set<GrupoMuscular>`.
- **Seis grupos del sistema** (BRAZOS, PIERNAS, PECHO, ESPALDA, CARDIO, ELONGACION) se crean al arranque en `DataInitializer` vía `GrupoMuscularService.asegurarGruposSistema()`.
- **ABM en panel profesor:** listar (sistema + propios), crear, editar y eliminar **solo** grupos propios (`profesor_id` no null). Rutas bajo `/profesor/mis-grupos-musculares`.
- **Vistas Thymeleaf:** en listados y filtros se usa la entidad (ej. `grupo.nombre`, `grupo.id`); en formularios de ejercicio se envían `grupoIds` (List&lt;Long&gt;) y el controlador resuelve a `Set<GrupoMuscular>`.
- **Export/Import:** se exportan e importan grupos por nombre; al importar se resuelven con `resolveGruposByNames` (sistema o del profesor).
- **Enum** `MuscleGroup` eliminado.

### 9.2 Archivos creados

| Archivo | Descripción |
|--------|-------------|
| `entidades/GrupoMuscular.java` | Entidad JPA con id, nombre, profesor (ManyToOne nullable). |
| `repositorios/GrupoMuscularRepository.java` | findByProfesorIsNull, findByProfesorId, findDisponiblesParaProfesor, findByNombreAndProfesorIsNull/Id. |
| `servicios/GrupoMuscularService.java` | findDisponiblesParaProfesor, resolveGruposByIds/ByNames, asegurarGruposSistema, guardar, eliminar, puedeSerEditadoPorProfesor. |
| `templates/profesor/grupos-musculares-lista.html` | Lista de grupos del sistema (solo lectura) y “Mis grupos” (editar/eliminar). |
| `templates/profesor/grupo-muscular-form.html` | Formulario crear/editar grupo (nombre). |

### 9.3 Archivos modificados (principales)

| Archivo | Cambio |
|--------|--------|
| `config/DataInitializer.java` | Llama a `grupoMuscularService.asegurarGruposSistema()` al arranque. |
| `entidades/Exercise.java` | `Set<GrupoMuscular> grupos` con @ManyToMany y tabla `exercise_grupos`. |
| `repositorios/ExerciseRepository.java` | `findByGruposContaining(GrupoMuscular)`, `findByGrupoId(Long)`. |
| `servicios/ExerciseService.java` | Uso de `Set<GrupoMuscular>`, `modifyExercise(..., grupos)`, `findExercisesByGrupo` / `findExercisesByGrupoId`. |
| `servicios/ExerciseCargaDefaultOptimizado.java` | Resuelve grupos por nombre con `findByNombreSistema` y asigna `Set.of(grupo)` a cada ejercicio. |
| `servicios/ExerciseExportImportService.java` | Export: `getGrupos().stream().map(GrupoMuscular::getNombre)`. Import: `resolveGruposByNames`. |
| `servicios/ExerciseAsignacionService.java` | Copia de ejercicio usa `getGrupos()` / `setGrupos()`. |
| `controladores/ProfesorController.java` | mis-ejercicios usa `gruposMusculares` y `grupoIds`; añadidas rutas `/mis-grupos-musculares` (listar, nuevo, crear, editar, actualizar, eliminar). |
| `controladores/ExerciseController.java` | `grupoId` (Long), `gruposMusculares`, `grupoIds`; inyectado `GrupoMuscularService`. |
| `controladores/SerieController.java` | Filtro por `grupoId`; modelo `gruposMusculares`, `selectedGrupoId`. |
| Templates (ejercicios, profesor, series) | Reemplazo de `muscleGroups`/enum por `grupos`, `grupo.nombre`, `grupo.id`, `grupoIds` en formularios. |

### 9.4 Archivo eliminado

- `enums/MuscleGroup.java`

### 9.5 Rutas ABM grupos musculares (profesor)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/profesor/mis-grupos-musculares` | Lista grupos sistema + propios. |
| GET | `/profesor/mis-grupos-musculares/nuevo` | Formulario alta. |
| POST | `/profesor/mis-grupos-musculares/nuevo` | Crear grupo (asignado al profesor). |
| GET | `/profesor/mis-grupos-musculares/editar/{id}` | Formulario edición (solo grupos propios). |
| POST | `/profesor/mis-grupos-musculares/editar/{id}` | Guardar cambios. |
| GET | `/profesor/mis-grupos-musculares/eliminar/{id}` | Eliminar (solo grupos propios). |

### 9.6 Enlaces en la UI

- **Dashboard profesor:** botón “Grupos Musculares” en la barra de acciones.
- **Mis Ejercicios:** enlace “Grupos Musculares” en el header.

---

*Documento de plan; implementación completada en Febrero 2026.*
