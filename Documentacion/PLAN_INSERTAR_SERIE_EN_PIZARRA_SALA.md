# Insertar serie en pizarra en sala

**Contexto:** [FASE_7_PANTALLA_DE_SALA.md](FASE_7_PANTALLA_DE_SALA.md) (pizarra digital para TV).  
**Estado:** Plan para implementación futura (opcional).

---

## 1. Idea

En la pizarra de sala cada **columna** corresponde a un usuario/alumno y contiene ejercicios con **nombre, peso y repeticiones** (o seg/min). Eso es conceptualmente igual a una **serie** (Serie + SerieEjercicio) que ya se usa para armar rutinas. La idea es permitir, desde el **panel de pizarra**, agregar de una vez una **serie completa** a una columna, en lugar de arrastrar ejercicio por ejercicio.

**Ejemplo:** El profesor elige la columna "Juan", pulsa "Agregar serie", selecciona la serie "Piernas base" y se insertan todos los ejercicios de esa serie en la columna de Juan, con su peso y reps ya cargados.

---

## 2. ¿Se puede implementar?

Sí. Los datos son compatibles:

| SerieEjercicio (serie) | PizarraItem (pizarra) |
|------------------------|------------------------|
| exercise                | exercise                |
| valor                   | repeticiones            |
| unidad (reps/min)       | unidad                  |
| peso                    | peso                    |
| orden                   | orden                   |

No hace falta cambiar el modelo de datos; solo copiar de una entidad a la otra al "pegar" la serie en una columna.

---

## 3. Plan de implementación (a futuro)

### 3.1 Backend

- **Endpoint nuevo**, por ejemplo:  
  `POST /profesor/pizarra/agregar-serie`  
  Body: `{ "columnaId": <id>, "serieId": <id> }`.
- **Lógica:**
  - Cargar la `Serie` con sus `SerieEjercicio` ordenados por `orden`.
  - Comprobar que la columna existe y pertenece a una pizarra del profesor.
  - Por cada `SerieEjercicio`, crear un `PizarraItem` en esa columna:
    - `exercise`, `peso`, `unidad`, `orden`
    - `repeticiones` = `SerieEjercicio.getValor()`.
  - Los ítems se agregan al final de la columna (siguiente orden disponible).
- **Listado de series:** Ya existe `SerieService.obtenerSeriesPlantillaPorProfesor(profesorId)`; se puede reutilizar o exponer un endpoint/listado para el selector (p. ej. `GET /profesor/pizarra/series-disponibles` que devuelva las series plantilla del profesor).

### 3.2 Frontend (editor de pizarra)

- Por columna (o en un lugar visible del editor): botón **"Agregar serie"**.
- Al hacer clic: abrir selector o modal con la lista de **series plantilla** del profesor (las mismas que se usan al armar rutinas).
- Al elegir una serie y confirmar: llamar al `POST` anterior; el backend devuelve la pizarra/columna actualizada y en el editor se refrescan las tarjetas de esa columna (igual que al agregar un ejercicio suelto).

### 3.3 Consideraciones

- **Inserción:** Primera versión: agregar la serie **al final** de la columna (orden = máximo actual + 1, 2, 3…). Opcional después: "insertar en posición X".
- **Unidad "seg":** La pizarra ya soporta reps / seg / min; las series hoy usan reps y min. Si en el futuro las series tuvieran segundos, solo habría que mapear esa unidad al crear el `PizarraItem`.

---

## 4. Resumen

- **Qué:** Poder insertar una serie (lista de ejercicios con peso/reps) en una columna de la pizarra de sala desde el panel del profesor.
- **Por qué:** Acelera el armado de la pizarra cuando un alumno usa una serie ya definida.
- **Estado:** Queda como **plan opcional** para un futuro; cuando se decida implementarlo, este documento sirve como guía.

---

*Documento creado Febrero 2026. Relacionado con Fase 7 (Pantalla de sala).*
