# Changelog – Orden de series en rutinas y orden de ejercicios en series (Febrero 2026)

Implementación del reordenamiento de **series dentro de una rutina** y de **ejercicios dentro de una serie**, con persistencia del orden y botones Subir/Bajar (flechas) en las interfaces.

---

## 1. Resumen de cambios

### Rutinas – Orden de las series

- En **crear rutina:** la lista "Series seleccionadas (orden en la rutina)" permite reordenar con botones **Subir** y **Bajar**. El orden en que quedan las series al guardar es el que se persiste.
- En **editar rutina:** cada serie en "Series en esta Rutina" tiene botones **Subir** y **Bajar** para cambiar su posición. Al guardar, el orden se actualiza.
- El campo **`Serie.orden`** (ya existente) se asigna al crear o actualizar las series de una rutina (0, 1, 2, …). La hoja de rutina pública y la vista al editar muestran las series ordenadas por `orden`.

### Series – Orden de los ejercicios en la serie

- En **crear/editar serie:** la tabla "Ejercicios en esta Serie" tiene en cada fila botones **Subir** y **Bajar** para cambiar la posición del ejercicio dentro de la serie.
- Se añadió el campo **`SerieEjercicio.orden`** para persistir la posición. Al crear o actualizar una serie, cada ejercicio recibe su índice (0, 1, 2, …) como `orden`.
- La vista **Ver serie** y la **hoja de rutina** (por token) muestran los ejercicios de cada serie ordenados por `orden`.

---

## 2. Cambios técnicos

### 2.1 Rutinas – Backend

| Archivo | Cambio |
|--------|--------|
| **RutinaService** | `agregarSerieARutina`: asigna `orden` con `serieRepository.countByRutinaId(rutinaId)`. `obtenerRutinaPorIdConSeries`: ordena `rutina.getSeries()` por `Serie.getOrden()`. `obtenerRutinaPorToken`: ordena series por `orden` y, para cada serie, ordena `serieEjercicios` por `orden`. `asignarRutinaPlantillaAUsuario`: copia series en orden usando `serieRepository.findByRutinaIdOrderByOrdenAsc` y `copiarSerieParaNuevaRutina(..., i)`. |
| **SerieService** | `copiarSerieParaNuevaRutina(Serie, Rutina, int orden)`: nuevo parámetro `orden`; asigna `nuevaSerie.setOrden(orden)` y al copiar `SerieEjercicio` asigna `orden` y ordena la lista origen por `orden`. |

### 2.2 Rutinas – Frontend

| Archivo | Cambio |
|--------|--------|
| **crearRutina.html** | Lista "Series seleccionadas (orden en la rutina)" con ítems que incluyen nombre, reps, **Subir**, **Bajar**. Al seleccionar una serie del carrusel se agrega a esta lista. El formulario envía `selectedSeries` y `repeticiones_XXX` en el orden de la lista. |
| **editarRutina.html** | Botones **Subir** y **Bajar** en cada ítem de "Series en esta Rutina". Al guardar, `seriesIds` y `repeticionesExistentes` se envían en el orden actual de las filas; el backend reasigna `orden` al reañadir las series. |

### 2.3 Series – Entidad y backend

| Archivo | Cambio |
|--------|--------|
| **SerieEjercicio.java** | Nuevo campo `Integer orden = 0` con getter/setter; `getOrden()` devuelve 0 si es null. |
| **SerieService** | `crearSeriePlantilla`: en el bucle sobre `serieDTO.getEjercicios()`, asigna `serieEjercicio.setOrden(i)`. `actualizarSeriePlantilla`: igual al recrear los `SerieEjercicio`. `obtenerSeriePorIdConEjercicios`: ordena `serie.getSerieEjercicios()` por `orden`. `convertirSerieADTO`: ordena por `orden` antes de mapear a DTO. `copiarSerieParaNuevaRutina`: ordena `serieOriginal.getSerieEjercicios()` por `orden` y asigna `nuevoSe.setOrden(i)`. |
| **SerieController** | `verSerie`: usa `obtenerSeriePorIdConEjercicios(id)` para mostrar ejercicios ordenados. |
| **RutinaService** | `obtenerRutinaPorToken`: además de ordenar series, ordena `serie.getSerieEjercicios()` por `orden` en cada serie (para la hoja pública). |

### 2.4 Series – Frontend

| Archivo | Cambio |
|--------|--------|
| **crearSerie.html** | Texto "Usá las flechas para cambiar el orden de los ejercicios." Columna "Orden / Acción" con **Subir**, **Bajar** y **Eliminar** en cada fila de la tabla de ejercicios. Eventos para mover la fila (insertBefore) al hacer clic en Subir/Bajar. Misma estructura en filas añadidas por clic en el carrusel y en modo edición. |

---

## 3. Base de datos

- **Serie:** el campo `orden` ya existía; se utiliza de forma consistente.
- **SerieEjercicio:** nueva columna **`orden`** (INT, puede ser NULL o DEFAULT 0). Si se usa `spring.jpa.hibernate.ddl-auto=update`, Hibernate puede crearla. Si no, ejecutar algo como:  
  `ALTER TABLE serie_ejercicio ADD COLUMN orden INT DEFAULT 0;`  
  Las filas existentes quedarán con `orden` 0 hasta que se editen y guarden de nuevo.

---

## 4. Ayuda memoria

- En **AYUDA_MEMORIA.md**, el ítem "Creación de rutina – Orden de las series" quedó marcado como **Implementado** con una nota breve.

---

*Documentación creada en Febrero 2026.*
