# Changelog – Rutinas, series y modificar rutina (Febrero 2026)

Documentación de los cambios realizados para **commit y push**. Incluye la corrección del guardado al modificar rutina y mejoras previas en el flujo de series/rutinas.

---

## 1. Corrección crítica: guardado al modificar rutina

### Problema
- Al **guardar** una rutina editada solo se conservaba **una serie** (o la última).
- Al **editar de nuevo** solo se mostraba una serie en "Series en esta Rutina".
- Al **Ver rutina** (hoja pública) seguían apareciendo las series anteriores, como si el cambio no se hubiera aplicado.

### Causa
En `RutinaService.actualizarSeriesDeRutina`:
1. Se **borraban** todas las series de la rutina (`serieRepository.deleteByRutinaId(rutinaId)`).
2. Luego se intentaba **re-añadir** las existentes con `serieRepository.findById(serieId)` usando los IDs del formulario. Esas series ya estaban eliminadas, por lo que `findById` devolvía `null` y no se re-añadía ninguna.
3. Solo se añadían las series "nuevas" (`nuevasSeriesIds`), por eso al final solo quedaba una o ninguna serie existente.

### Solución implementada
**Archivo:** `src/main/java/com/mattfuncional/servicios/RutinaService.java`

1. **Antes de borrar:** para cada serie existente (según `seriesIds` y `repeticionesExistentes` del formulario) se carga la serie, se obtiene su `plantillaId` (o su propio `id` si es plantilla) y las repeticiones, y se guarda en dos listas: `plantillaIdsAReañadir` y `repsAReañadir`.
2. Se borran las series antiguas de la rutina (igual que antes).
3. Se **re-añaden** todas las series existentes usando esas listas con `agregarSerieARutina(rutinaId, plantillaId, repeticiones)`.
4. Se añaden las series nuevas (`nuevasSeriesIds` + `repeticionesNuevas`) como ya se hacía.

Con esto, al guardar se mantienen todas las series que ya tenía la rutina (con sus reps actualizadas) más las que se agregaron desde "Añadir más Series".

---

## 2. Mejoras previas en el flujo de series y rutinas

### 2.1 Crear rutina
- Botón **"Modificar"** en cada tarjeta de serie para editar la serie desde la creación de rutina.
- Al abrir "Modificar" se cargan correctamente los ejercicios de la serie (`SerieRepository.findByIdWithSerieEjercicios`, `SerieService.obtenerSeriePorIdConEjercicios`; controlador pasa `serieDTOJson` en `series/crearSerie.html`).

### 2.2 Modificar rutina (editar)
- Título de la vista: **"Modificar Rutina"** (`rutinas/editarRutina.html`).
- Carga de series al editar: `RutinaRepository.findByIdWithSeries`, `RutinaService.obtenerRutinaPorIdConSeries`; la sección "Series en esta Rutina" se rellena correctamente.
- **Layout:** izquierda = Detalles de la rutina; derecha arriba = "Series en esta Rutina"; abajo (ancho completo) = "Añadir más Series".
- **Filtro de series disponibles:** se excluyen por `plantillaId` o por `serie.getId()` si no hay plantilla (`RutinaControlador`).
- Reps en las tarjetas de "Añadir más Series" usan `plantilla.repeticionesSerie` (no valor fijo).
- Selección por **clic en la tarjeta** (sin botón "Agregar"); tarjeta seleccionada se pinta en **amarillo/naranja** (`.serie-card.selected`).
- **Vista previa** "Se agregarán al guardar" con el mismo formato que las series actuales (nombre, Reps, Ver, Quitar); inputs `nuevasSeriesIds` y `repeticionesNuevas`; Quitar deselecciona la tarjeta.

### 2.3 Vista "Mis Series" (dashboard profesor)
- En la tabla de series: botón **"Ver serie"** en Acciones.
- Eliminada la columna ID de la tabla.

### 2.4 Volver al tab correcto tras guardar
- Parámetro `tab` en la URL (`?tab=series`, `?tab=rutinas`, `?tab=asignaciones`).
- En `profesor/dashboard.html` un script activa la pestaña según el `tab` de la query string.
- En `ProfesorController` el redirect a `/profesor/{id}` conserva la query string.
- Redirecciones desde series, rutinas y asignar-rutina incluyen el `tab` adecuado.

### 2.5 Logo en navbars
- Todas las referencias al logo pasan a **`logo matt.jpeg`** en `static/img/`: `fragments/navbar.html`, `profesor/dashboard.html`, `series/crearSerie.html`, `index.html`, `demo.html`, `panelprofesor.html`.

### 2.6 Crear/editar serie
- En el flujo de crear serie se añaden `editMode` y `serieDTOJson` al modelo para evitar error en el template.
- Título y logo en `crearSerie.html` corregidos para que la página cargue y muestre ejercicios correctamente.

---

## 3. Archivos modificados en esta iteración (guardado rutina)

| Archivo | Cambio |
|--------|--------|
| **RutinaService.java** | `actualizarSeriesDeRutina`: resolver `plantillaId` y repeticiones **antes** de borrar series; re-añadir existentes desde listas guardadas; añadir `ArrayList` en imports. |

---

## 4. Sugerencia de mensaje para commit

```
fix(rutinas): corregir guardado al modificar rutina (conservar todas las series)

- RutinaService.actualizarSeriesDeRutina: obtener plantillaId y reps de cada
  serie existente ANTES de borrarlas; re-añadir todas (existentes + nuevas)
  después del borrado para que no se pierdan series al guardar.
- Documentación: CHANGELOG_RUTINAS_SERIES_FEB2026.md y actualización
  PLAN_DE_DESARROLLO.md (sección 2.6).
```

---

*Documento generado para commit/push – Febrero 2026.*
