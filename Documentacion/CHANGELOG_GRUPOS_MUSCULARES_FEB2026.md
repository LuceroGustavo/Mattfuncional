# Changelog – Grupos musculares como entidad (Febrero 2026)

Mejora: el profesor puede crear grupos musculares propios y usarlos en ejercicios, series y filtros. El grupo muscular deja de ser un enum fijo y pasa a ser una entidad en base de datos.

---

## 1. Resumen del cambio

- **Antes:** Grupo muscular era un **enum** `MuscleGroup` (BRAZOS, PIERNAS, PECHO, ESPALDA, CARDIO, ELONGACION). No se podían añadir grupos nuevos sin cambiar código.
- **Ahora:** Grupo muscular es la **entidad `GrupoMuscular`** (tabla `grupo_muscular`). Los 6 grupos anteriores existen como “grupos del sistema” (`profesor_id = null`) y se crean al arranque. El profesor puede **crear, editar y eliminar** sus propios grupos desde el panel (“Grupos Musculares”). En ejercicios, series y filtros se usan siempre entidades (por id y nombre).

---

## 2. Cambios técnicos principales

### 2.1 Modelo de datos
- **Entidad `GrupoMuscular`:** id, nombre, profesor (ManyToOne, nullable). Restricción única (nombre, profesor_id).
- **Exercise:** de `@ElementCollection Set<MuscleGroup>` a `@ManyToMany Set<GrupoMuscular>` con tabla `exercise_grupos`.
- **Inicialización:** en `DataInitializer` se llama a `GrupoMuscularService.asegurarGruposSistema()` y se crean los 6 grupos del sistema si no existen.

### 2.2 Servicios
- **GrupoMuscularService:** listar grupos sistema/propios, disponibles para profesor, resolver por IDs o por nombres, guardar, eliminar, comprobar si un grupo puede ser editado por el profesor.
- **ExerciseService / ExerciseCargaDefaultOptimizado / ExportImport / Asignacion:** uso de `Set<GrupoMuscular>` y resolución por nombre o id donde corresponde.

### 2.3 Controladores y rutas
- **ProfesorController:** rutas `/profesor/mis-grupos-musculares` (listar, nuevo, crear, editar, actualizar, eliminar). En mis-ejercicios se envían `gruposMusculares` y en POST `grupoIds`.
- **ExerciseController y SerieController:** filtro por `grupoId` (Long); modelo con `gruposMusculares` y `selectedGrupoId` / `selectedGrupoId`.

### 2.4 Vistas (Thymeleaf)
- Listados y filtros: se usa la entidad (ej. `grupo.nombre`, `grupo.id`).
- Formularios de ejercicio: checkboxes con `name="grupoIds"`, `value="${grupo.id}"`; en edición, `th:checked` según `exercise.grupos`.
- **Nuevas vistas:** `profesor/grupos-musculares-lista.html` (lista sistema + mis grupos con editar/eliminar) y `profesor/grupo-muscular-form.html` (crear/editar grupo).

### 2.5 Export/Import
- Export: se guardan los nombres de los grupos del ejercicio (`grupos.![nombre]`).
- Import: se resuelven grupos por nombre (primero sistema, luego del profesor) con `resolveGruposByNames`.

### 2.6 Eliminado
- Enum `MuscleGroup` y todas sus referencias.

---

## 3. Rutas nuevas (ABM grupos musculares)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/profesor/mis-grupos-musculares` | Lista grupos del sistema + grupos propios del profesor. |
| GET | `/profesor/mis-grupos-musculares/nuevo` | Formulario de alta de grupo. |
| POST | `/profesor/mis-grupos-musculares/nuevo` | Crear grupo (asignado al profesor). |
| GET | `/profesor/mis-grupos-musculares/editar/{id}` | Formulario de edición (solo grupos propios). |
| POST | `/profesor/mis-grupos-musculares/editar/{id}` | Guardar cambios. |
| GET | `/profesor/mis-grupos-musculares/eliminar/{id}` | Eliminar grupo (solo grupos propios). |

Solo se pueden editar o eliminar grupos cuyo `profesor_id` coincide con el profesor logueado. Los grupos del sistema son solo lectura en el ABM.

---

## 4. Enlaces en la interfaz

- **Dashboard del profesor:** botón “Grupos Musculares” en la barra de acciones principales.
- **Mis Ejercicios:** enlace “Grupos Musculares” en el header de la página.

---

## 5. Archivos creados

- `entidades/GrupoMuscular.java`
- `repositorios/GrupoMuscularRepository.java`
- `servicios/GrupoMuscularService.java`
- `templates/profesor/grupos-musculares-lista.html`
- `templates/profesor/grupo-muscular-form.html`
- `Documentacion/CHANGELOG_GRUPOS_MUSCULARES_FEB2026.md` (este archivo)

## 6. Archivo eliminado

- `enums/MuscleGroup.java`

---

## 7. Documentación actualizada

- **PLAN_GRUPOS_MUSCULARES_ENTIDAD.md:** estado “IMPLEMENTADO” y nueva sección 9 con detalle de la implementación.
- **PLAN_DE_DESARROLLO.md:** sección 2.8 actualizada (grupos musculares implementado) y referencia a este changelog.

---

*Changelog para commit/push de la mejora “Grupos musculares como entidad” (Febrero 2026).*
