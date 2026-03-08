# Series: repetir el mismo ejercicio en una serie

**Estado:** Implementado (Marzo 2026)

**Vistas:** Crear serie (`/series/crear`), Editar serie (`/series/editar/{id}`)

---

## 1. Resumen

En crear/editar serie plantilla se permite **añadir el mismo ejercicio varias veces** a la misma serie, cada vez con **cantidad**, **unidad** (repeticiones/minutos/segundos) y **peso** distintos. Antes solo se permitía una entrada por ejercicio.

---

## 2. Comportamiento

- **Carrusel de ejercicios:** cada clic en una tarjeta añade **una fila** a la tabla "Ejercicios en esta Serie", aunque sea el mismo ejercicio.
- **Tabla:** cada fila tiene ejercicio, cantidad, unidad, peso y acciones (subir/bajar/eliminar). Se pueden reordenar con las flechas.
- **Estado visual:** la tarjeta del ejercicio se marca como "selected". Al eliminar una fila, la tarjeta solo pierde "selected" si ya no queda ninguna fila de ese ejercicio.
- **Guardar:** el backend ya soportaba varias entradas del mismo ejercicio (lista de `SerieEjercicio` / `EjercicioSerieDTO`); no hubo cambios en DTO ni entidades.

---

## 3. Cambios técnicos (frontend)

| Archivo | Cambio |
|---------|--------|
| `series/crearSerie.html` | Eliminado el bloqueo por `ejerciciosSeleccionados.has(id)` que impedía repetir ejercicio. Eliminado el `Set` y la lógica asociada. |
| `series/crearSerie.html` | Al eliminar una fila, se quita "selected" del card solo si no queda ninguna otra fila con ese `data-id`. |
| `series/crearSerie.html` | Un solo manejador de clic para añadir fila: listener **delegado** en el carrusel. Eliminado el `slide.addEventListener` dentro de `renderCarrusel` para evitar que un clic insertara 2 filas. |
| `series/crearSerie.html` | Añadida opción "Segundos" en el select de unidad de la fila que se inserta por el listener del carrusel. |

---

## 4. Archivos sin cambios

- **SerieDTO / EjercicioSerieDTO:** ya permitían lista de ejercicios con mismo `ejercicioId` y distintos valor/unidad/peso.
- **SerieEjercicio (entidad):** relación N:1 con Exercise; una serie tiene muchas filas, cada una con su ejercicio y parámetros.
- **SerieService.crearSeriePlantilla / actualizarSeriePlantilla:** recorren la lista del DTO y crean un `SerieEjercicio` por ítem.
