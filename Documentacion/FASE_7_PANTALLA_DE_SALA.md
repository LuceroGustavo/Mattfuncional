# Fase 7 – Pantalla de sala (Pizarra digital para TV)

**Para contexto del proyecto:** [LEEME_PRIMERO.md](LEEME_PRIMERO.md).  
**Plan de desarrollo:** [PLAN_DE_DESARROLLO_UNIFICADO.md](PLAN_DE_DESARROLLO_UNIFICADO.md).

**Estado:** En desarrollo (primera versión implementada)  
**Última actualización:** Febrero 2026

---

## 1. Objetivo

Reemplazar la pizarra física donde el profesor escribe los ejercicios por una **pizarra digital** mostrada en un televisor. Los alumnos ven los ejercicios asignados a su columna/bloque. El profesor controla todo desde el panel (PC/tablet).

---

## 2. Requisitos funcionales

### 2.1 Columnas / Bloques

| # | Requisito | Detalle |
|---|-----------|---------|
| R1 | Cantidad de columnas | La pizarra se divide en **1 a 6 columnas**. El profesor elige la cantidad al abrir/crear la pizarra (hasta 6 alumnos por turno). |
| R2 | Título por columna | Cada columna tiene un **título editable** arriba (ej: "Pablo", "Juan/Edu") para identificar a quién van los ejercicios. |

### 2.2 Ejercicios

| # | Requisito | Detalle |
|---|-----------|---------|
| R3 | Selección desde lista | Los ejercicios se eligen de una **lista con filtros** (nombre, grupo muscular), similar a crear series (`crearSerie.html`). |
| R4 | Arrastrar y soltar | Proceso **dinámico**: una vez definidas las columnas, se **arrastran** ejercicios desde la lista a cada bloque. Se acomodan automáticamente. |
| R5 | Edición directa | Se trabaja **directamente en la pizarra**, no en un formulario previo que arme la pizarra. |
| R7 | Contenido de la tarjeta | Cada tarjeta muestra: **nombre del ejercicio**, **peso** (kg), **repeticiones** (o minutos). Peso y repeticiones **editables directamente en la tarjeta**. |

### 2.3 Persistencia y visualización

| # | Requisito | Detalle |
|---|-----------|---------|
| R6 | Guardar pizarra | Opción de **guardar** el estado. Se puede **cargar** una pizarra guardada otro día. |
| R8 | Visualización en TV | La pizarra se abre en una **pestaña HTML**; con **F11** (fullscreen) se ve bien en un TV compartido por cable/DLNA/WiFi. |

---

## 3. Modelo de datos propuesto

### 3.1 Entidades nuevas

```
Pizarra (pizarra)
├── id
├── profesor_id (FK)
├── nombre (ej: "Lunes 18hs", "Turno mañana")
├── token (único, para URL de sala)
├── cantidad_columnas (1-6)
├── fecha_creacion
└── fecha_ultima_modificacion

PizarraColumna (pizarra_columna)
├── id
├── pizarra_id (FK)
├── titulo (ej: "Pablo", "Juan/Edu")
├── orden (0, 1, 2... para posición de la columna)
└── [lista de items]

PizarraItem (pizarra_item)
├── id
├── pizarra_columna_id (FK)
├── exercise_id (FK)
├── peso (Integer, kg)
├── repeticiones (Integer) o minutos si unidad = "min"
├── unidad ("reps" | "min")
└── orden (posición dentro de la columna)
```

**Nota:** `Exercise` ya existe. `SerieEjercicio` tiene `valor`, `unidad`, `peso` como referencia; aquí usamos campos similares.

### 3.2 Relaciones

- Una **Pizarra** tiene N **PizarraColumna** (1 a 6).
- Una **PizarraColumna** tiene N **PizarraItem** (ejercicios con peso y reps).

---

## 4. Flujos de usuario

### 4.1 Profesor – Crear/Editar pizarra

1. Entra a "Pizarra" o "Pantalla de sala" desde el panel.
2. Elige **crear nueva** o **cargar** una pizarra guardada.
3. Si es nueva: selecciona **cantidad de columnas** (1-6).
4. Escribe el **título** de cada columna (nombres de alumnos/grupos).
5. Abre el **listado de ejercicios** (con filtros como en crear serie).
6. **Arrastra** ejercicios a cada columna.
7. **Edita** peso y repeticiones directamente en cada tarjeta.
8. **Guarda** la pizarra (nombre opcional para identificarla).
9. Botón **"Abrir en TV"** o **"Copiar enlace"** → URL tipo `/sala/{token}`.

### 4.2 TV – Visualización

1. Se abre la URL `/sala/{token}` en el navegador del TV.
2. Se presiona **F11** para fullscreen.
3. La vista muestra las columnas con títulos y tarjetas de ejercicios.
4. **Polling:** cada X segundos (ej: 10-15) se actualiza el contenido si el profesor modificó la pizarra.

---

## 5. Rutas y seguridad

| Ruta | Acceso | Descripción |
|------|--------|-------------|
| `/profesor/pizarra` | ADMIN, AYUDANTE, DEVELOPER | Lista de pizarras guardadas + crear nueva |
| `/profesor/pizarra/editar/{id}` | idem | Editor de pizarra (arrastrar, editar tarjetas) |
| `/profesor/pizarra/nueva` | idem | Crear pizarra (elegir columnas, títulos) |
| `/sala/{token}` | `permitAll` | Vista solo lectura para TV (sin login) |
| `GET /api/sala/{token}/estado` | `permitAll` | JSON con estado actual (para polling) |
| `POST /api/profesor/pizarra/*` | autenticado | Guardar, actualizar, crear items |

---

## 6. Consideraciones técnicas

### 6.1 Frontend – Editor de pizarra

- **Drag and drop:** HTML5 Drag and Drop API o librería (SortableJS, etc.).
- **Lista de ejercicios:** Reutilizar lógica/filtros de `crearSerie.html` (filtro por nombre, grupo muscular).
- **Edición inline:** Peso y reps editables con `contenteditable` o inputs pequeños en la tarjeta.
- **Auto-guardado opcional:** O botón "Guardar" explícito.

### 6.2 Vista TV

- HTML/CSS responsive para pantalla grande.
- **Polling:** `setInterval` cada 10-15 segundos a `GET /api/sala/{token}/estado`.
- Sin controles de edición; solo visualización.
- Diseño similar al esquema: columnas, títulos en rectángulo negro, tarjetas con grupo muscular, imagen, nombre, peso, reps.

### 6.3 Token

- Generar token único al crear pizarra (como en `Rutina` para la hoja pública).
- Una pizarra = un token. Si se "carga" una guardada, puede ser la misma pizarra (mismo token) o una copia (nuevo token).

---

## 7. Preguntas / Mejoras sugeridas

1. **Varias pizarras guardadas:** ¿Un profesor puede tener múltiples pizarras guardadas (ej: "Lunes 18hs", "Martes 10hs") y elegir cuál cargar? → **Asumido: sí.**

2. **Copia vs. reutilizar:** Al cargar una pizarra guardada, ¿se edita la misma (sobrescribe) o se crea una copia para ese día? → **Propuesta:** editar la misma; si quiere "plantilla" para otro día, que duplique explícitamente.

3. **Vista TV – solo la activa:** ¿La URL `/sala/{token}` muestra siempre la pizarra asociada a ese token? Si el profesor cambia de pizarra (carga otra), ¿el TV tendría que abrir otra URL? → **Asumido:** cada pizarra tiene su token; el TV muestra la que corresponde a la URL abierta. Si el profesor quiere mostrar otra, abre otra pestaña/URL o cambia la pizarra "en vivo" (mismo token, actualiza contenido).

4. **Orden de columnas:** ¿Se puede reordenar columnas (arrastrar columna completa)? → **Fase inicial:** no; orden fijo. Opcional para después.

5. **Imagen del ejercicio:** La tarjeta debe mostrar la imagen del ejercicio (como en el esquema). `Exercise` tiene `Imagen`; reutilizar la misma lógica que en series/hoja de rutina.

---

## 8. Tareas / Checklist de implementación

### Fase 8.1 – Backend

- [ ] Crear entidades `Pizarra`, `PizarraColumna`, `PizarraItem`
- [x] Repositorios JPA
- [x] Servicios: crear, guardar, cargar, obtener por token
- [x] Controlador REST: `GET /api/sala/{token}/estado` (JSON)
- [x] Controlador profesor: rutas para listar, crear, editar pizarra
- [x] Generar token único al crear pizarra
- [x] SecurityConfig: `permitAll` para `/sala/**` y `/api/sala/**`

### Fase 8.2 – Editor de pizarra (panel profesor)

- [x] Vista lista de pizarras guardadas
- [x] Vista crear pizarra: selector 1-6 columnas, títulos
- [x] Vista editor: columnas + área de ejercicios con filtros
- [x] Drag and drop ejercicios a columnas
- [x] Tarjetas con peso y reps editables
- [x] Botón Guardar
- [x] Botón "Copiar enlace" / "Abrir en TV"

### Fase 8.3 – Vista TV

- [x] Template `sala.html` con layout de columnas
- [x] Tarjetas: grupo muscular, imagen, nombre, peso, reps
- [x] Polling para actualizar estado (15 s)
- [x] Estilos fullscreen-friendly

### Fase 8.4 – Integración y pruebas

- [ ] Probar flujo completo: crear → guardar → abrir en TV
- [ ] Probar edición en vivo y actualización en TV
- [ ] Documentar en AVANCES_DEL_APP y CHANGELOG

---

## 9. Archivos a crear

| Tipo | Ruta |
|------|------|
| Entidad | `entidades/Pizarra.java` |
| Entidad | `entidades/PizarraColumna.java` |
| Entidad | `entidades/PizarraItem.java` |
| Repo | `repositorios/PizarraRepository.java` |
| Repo | `repositorios/PizarraColumnaRepository.java` |
| Repo | `repositorios/PizarraItemRepository.java` |
| Servicio | `servicios/PizarraService.java` |
| Controlador | `controladores/PizarraController.java` |
| Controlador | `controladores/SalaController.java` (vista TV + API) |
| Template | `templates/profesor/pizarra-lista.html` |
| Template | `templates/profesor/pizarra-editor.html` |
| Template | `templates/sala/sala.html` |

---

## 10. Notas para continuar desde otra PC

- **Contexto:** Este plan define la Fase 7 del proyecto Mattfuncional. La pizarra reemplaza la pizarra física del gimnasio.
- **Referencia visual:** Ver esquema en `assets/` (imagen de columnas con "Pablo", "Juan/Edu", tarjetas BRAZOS, Curl de Biceps, etc.).
- **Reutilizar:** Lógica de filtros de ejercicios en `crearSerie.html`; estructura de `SerieEjercicio` (valor, unidad, peso) para los items.
- **Orden sugerido:** Backend primero (entidades, repos, servicios, API), luego editor, luego vista TV.

### Implementado (primera versión, feb 2026)

- Entidades: `Pizarra`, `PizarraColumna`, `PizarraItem`. Tablas: `pizarra`, `pizarra_columna`, `pizarra_item`.
- Rutas: `/profesor/pizarra` (lista), `/profesor/pizarra/nueva`, `/profesor/pizarra/editar/{id}`, `/sala/{token}` (TV).
- API: `GET /sala/api/{token}/estado` para polling.
- Editor: arrastrar ejercicios a columnas, editar títulos, peso y reps en tarjeta, guardar.
- Vista TV: layout de columnas, polling cada 15 s, estilos oscuros para pantalla.
