# Fase 7 – Pantalla de sala (Pizarra digital para TV)

**Para contexto del proyecto:** [LEEME_PRIMERO.md](LEEME_PRIMERO.md).  
**Plan de desarrollo:** [PLAN_DE_DESARROLLO_UNIFICADO.md](PLAN_DE_DESARROLLO_UNIFICADO.md).

**Estado:** **Completada**  
**Última actualización:** Febrero 2026

La Fase 7 se considera **completa**: editor de pizarra con panel de ejercicios, columnas (1–6), arrastrar y soltar, peso/reps/unidad (reps, seg, min), persistencia, vista TV con polling, transmisión por token/código opcional, y ajustes de UI (logo circular en navbar, tarjetas en dos filas, scroll horizontal de columnas).

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
| `GET /sala/api/{token}/estado` | `permitAll` | JSON con estado actual (para polling) |
| `POST /profesor/pizarra/actualizar-basico` | autenticado | Nombre y títulos de columnas |
| `POST /profesor/pizarra/agregar-item` | autenticado | Añadir ejercicio a columna |
| `POST /profesor/pizarra/actualizar-item` | autenticado | Peso y repeticiones de un item |
| `POST /profesor/pizarra/eliminar-item` | autenticado | Quitar ejercicio de columna |
| `POST /profesor/pizarra/agregar-columna` | autenticado | Añadir columna (máx. 6) |
| `POST /profesor/pizarra/quitar-columna` | autenticado | Quitar columna (mín. 1) |

---

## 6. Consideraciones técnicas

### 6.1 Frontend – Editor de pizarra

- **Drag and drop:** HTML5 Drag and Drop API o librería (SortableJS, etc.).
- **Lista de ejercicios:** Reutilizar lógica/filtros de `crearSerie.html` (filtro por nombre, grupo muscular).
- **Edición inline:** Peso y reps editables con `contenteditable` o inputs pequeños en la tarjeta.
- **Auto-guardado opcional:** O botón "Guardar" explícito.

### 6.2 Vista TV

- HTML/CSS responsive para pantalla grande.
- **Actualización manual:** botón **"Actualizar"** en la barra superior (misma fila que logo y título). Al hacer clic se consulta `GET /sala/api/{token}/estado` y se redibuja el contenido. Sin polling automático para no consumir recursos; el usuario actualiza cuando el profesor indica que hubo cambios.
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
- [x] Botón "Actualizar" para refrescar contenido (sin polling automático)
- [x] Estilos fullscreen-friendly
- [x] Agregar columna / Quitar columna desde el editor (máx. 6, mín. 1)

### Fase 8.4 – Integración y pruebas

- [x] Probar flujo completo: crear → guardar → abrir en TV
- [x] Probar edición en vivo y actualización en TV
- [x] Documentar en AVANCES_DEL_APP y CHANGELOG

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
- Vista TV: layout de columnas, botón "Actualizar" en la barra (logo + título + botón), estilos oscuros para pantalla.

---

## 11. Mejoras implementadas (pulido Feb 2026)

### 11.1 Peso y repeticiones visibles en la TV

- **Problema:** Al editar peso/reps en el editor no se guardaban y en la vista TV aparecía "-".
- **Causa:** Las peticiones POST del editor no enviaban el token CSRF; Spring rechazaba las peticiones.
- **Solución:** Metas `_csrf` y `_csrf_header` en el editor; función `postHeaders()` que incluye el token en todas las peticiones POST (actualizar-basico, agregar-item, actualizar-item, eliminar-item).

### 11.2 Títulos de columnas que no se borran al agregar ejercicio

- **Problema:** Al arrastrar un ejercicio a una columna se hacía `location.reload()`; los títulos escritos (aún no guardados) desaparecían.
- **Solución:** Al soltar un ejercicio ya no se recarga la página: se crea la tarjeta en el DOM con los datos del ejercicio arrastrado y se enlazan los eventos (peso/reps, eliminar). Los títulos permanecen.

### 11.3 Títulos y nombre actualizados en la TV

- **Problema:** Los títulos de columna solo se enviaban al hacer clic en "Guardar"; en la TV no se veían hasta guardar.
- **Solución:** Auto-guardado de nombre y títulos: al salir del campo (blur) y con debounce de 500 ms al escribir. Backend: `columnaRepository.saveAll(cols)` tras actualizar títulos; controlador convierte la lista `titulos` del JSON a `List<String>` de forma segura.

### 11.4 Vista TV – Actualización manual (sin polling)

- **Antes:** Polling cada 2,5 s para actualizar la vista TV (más consumo de red/servidor si la pantalla queda abierta horas).
- **Ahora:** Botón **"Actualizar"** en la barra superior (misma fila que logo y título). Quien esté frente a la TV pulsa cuando el profesor avisa que cambió la pizarra. Sin peticiones en segundo plano.

### 11.5 Agregar y quitar columnas desde el editor

- **Agregar columna:** Botón "Agregar columna" (visible si hay menos de 6). POST `/profesor/pizarra/agregar-columna` con `pizarraId`; recarga el editor.
- **Quitar columna:** Botón ✕ en el título de cada columna (solo si hay más de 1). POST `/profesor/pizarra/quitar-columna` con `columnaId`; se elimina la columna y sus items, se reordena el resto y se actualiza `cantidadColumnas`.
- **Backend:** `PizarraService.agregarColumna(pizarraId, profesorId)` y `quitarColumna(columnaId, profesorId)`.

### 11.6 Corrección vista de login

- **Problema:** En `/login` se mostraba la página por defecto de Spring ("Please sign in") en lugar de la plantilla personalizada ("Iniciar Sesión").
- **Causa:** `PortalControlador` devolvía `"login.html"`; Thymeleaf espera el nombre de vista sin extensión.
- **Solución:** Devolver `"login"` (y `"index"`, `"demo"` en sus rutas) en `PortalControlador.java`.

### 11.7 Arranque de la aplicación (PizarraService)

- **Problema:** `ClassNotFoundException: Profesor` / `PizarraItem` al iniciar (RestartClassLoader de DevTools).
- **Solución:** En `PizarraService.java` se reemplazó `import com.mattfuncional.entidades.*` por imports explícitos de `Exercise`, `GrupoMuscular`, `Pizarra`, `PizarraColumna`, `PizarraItem`, `Profesor`.

---

## 12. Cambios recientes – Panel de trabajo y conexión global (Feb 2026)

Esta sección documenta la evolución de la pizarra hacia un **panel de trabajo único** y una **conexión global por profesor** para la TV.

### 12.1 Entrada directa al panel de trabajo

- **Antes:** Al entrar a "Pizarra" desde el panel del profesor se mostraba la **lista** de pizarras guardadas; desde ahí se creaba una nueva (formulario nombre + columnas) o se editaba una existente.
- **Ahora:** Al entrar a **Pizarra** (`/profesor/pizarra`) se redirige al **panel de trabajo** (`/profesor/pizarra/panel`), que abre directamente el editor con una pizarra de trabajo (4 columnas, nombre "Panel en vivo"). No hay formulario previo.
- **Pizarra de trabajo:** Una por profesor. Se obtiene o crea con `PizarraService.getOrCreatePizarraTrabajo(profesor)`. Persistencia en tabla `pizarra_trabajo` (entidad `PizarraTrabajo`: `profesor_id`, `pizarra_id`).
- **Lista de pizarras guardadas:** Sigue disponible en **/profesor/pizarra/lista** ("Administrar pizarras"). Desde el editor hay enlace "Administrar pizarras" y "Volver" (vuelve al panel).

### 12.2 Conexión global para la TV (un enlace por profesor)

- **Antes:** Cada pizarra tenía su propio token; la URL de la TV era distinta para cada pizarra. Si el profesor cambiaba de pizarra, en el TV había que abrir otra URL.
- **Ahora:** Existe una **sala de transmisión por profesor** (entidad `SalaTransmision`: `profesor_id`, `token`, `pin_sala_hash`, `pizarra_id`). Un solo **token/enlace** por profesor. La TV abre siempre la misma URL; el profesor asigna qué pizarra se muestra (la que está editando en el panel). Puede cambiar de pizarra (p. ej. con "Insertar pizarra existente") y la TV sigue con el mismo enlace.
- **Transmitir en TV:** Al pulsar "Transmitir en TV" se obtiene o crea la `SalaTransmision` del profesor, se asigna la pizarra actual (`pizarra_id`) y opcionalmente un PIN de 4 dígitos. El modal muestra el enlace (siempre el mismo hasta que se cambie). Si ya hay enlace/código configurado, al reabrir el modal se muestra el enlace y el mensaje de que el código está configurado (no se muestra por seguridad).
- **Nuevo enlace:** Al **cerrar sesión y volver a entrar**, se genera un **nuevo token** para la sala (el enlace anterior deja de funcionar). Implementado en `CustomAuthenticationSuccessHandler`: tras login exitoso del profesor se llama a `PizarraService.rotarTokenSala(profesorId)`.
- **Cambiar enlace manualmente:** En el modal "Transmitir en TV" hay botón **"Cambiar enlace (generar uno nuevo)"**. Genera un token nuevo, muestra la nueva URL y se debe configurar de nuevo el código (4 dígitos) si se desea. Endpoint: `POST /profesor/pizarra/transmitir/nuevo-enlace` con `pizarraId`.

### 12.3 Insertar pizarra existente

- **Funcionalidad:** En el editor hay botón **"Insertar pizarra existente"**. Abre un modal con el listado de todas las pizarras del profesor. Al elegir una, se **reemplaza el contenido** de la pizarra actual (panel) por el de la elegida: mismo número de columnas, títulos e ítems (clonación).
- **Backend:** `POST /profesor/pizarra/clonar` con `pizarraOrigenId` y `pizarraDestinoId`. `PizarraService.clonarPizarra(origenId, destinoId, profesorId)`. Se limpia la colección `destino.getColumnas()` antes de borrar columnas en BD para evitar `ObjectDeletedException` de Hibernate al hacer merge.

### 12.4 Vista TV: sin botón Actualizar y sin refresco automático

- **Antes:** En la vista TV (`sala.html`) había botón "Actualizar" y en algún momento existió polling automático cada pocos segundos.
- **Ahora:** En la vista TV **no hay botón Actualizar**. El contenido solo se carga al **abrir la página**. Para ver cambios, quien esté frente al TV debe **actualizar la página (F5)** cuando el profesor lo indique. Se añadieron meta `Cache-Control`, `Pragma` y `Expires` para evitar caché del navegador y que siempre se sirva la versión actual del template.

### 12.5 Botón "Actualizar en TV" en el panel del profesor

- **Ubicación:** En el editor de pizarra (panel), junto a "Transmitir en TV" e "Insertar pizarra existente".
- **Comportamiento:** Al hacer clic se **sincroniza** el estado actual (nombre y títulos de columnas) con el servidor (`actualizar-basico`) y se muestra el mensaje: *"Contenido enviado a la TV. Para ver los cambios en la pantalla del TV, actualizá la página en el navegador del TV (tecla F5)."* Así el profesor confirma que guardó y sabe que en el TV hay que actualizar con F5.

### 12.6 Guardar como nueva pizarra (no reemplazar)

- **Antes:** Al pulsar "Guardar" se actualizaba la pizarra actual (nombre y títulos); si el profesor guardaba de nuevo con otro nombre, se sobrescribía la misma pizarra.
- **Ahora:** Al pulsar **"Guardar"** se abre un modal para escribir el **nombre de la nueva pizarra**. Al confirmar:
  1. Se sincroniza el estado actual del panel con la pizarra en la que se está editando (`actualizar-basico`).
  2. Se **crea una nueva pizarra** con el nombre indicado y se **copia** en ella el contenido actual (columnas e ítems). La pizarra actual (panel) no se reemplaza; el profesor sigue editando en la misma.
- **Efecto:** Cada vez que guarda con un nombre distinto se **genera una pizarra nueva** en la lista ("Administrar pizarras"). Endpoint: `POST /profesor/pizarra/guardar-como-nueva` con `pizarraId` y `nombre`. Servicio: `PizarraService.guardarComoNuevaPizarra(pizarraOrigenId, nuevoNombre, profesorId)` (crea pizarra nueva y llama a `clonarPizarra`).

### 12.7 Resolución del estado en la sala (TV)

- La API `GET /sala/api/{token}/estado` y las vistas `/sala/{token}` y verificación de PIN resuelven el token así:
  - Primero se busca en **SalaTransmision** por token. Si existe, se usa `pizarra_id` de esa fila para construir el estado (y el PIN de la sala).
  - Si no hay SalaTransmision con ese token, se usa el token como **token de Pizarra** (comportamiento anterior: una pizarra con ese token). Así se mantiene compatibilidad con pizarras antiguas que tenían su propio token.

### 12.8 Archivos y rutas añadidos/modificados

| Tipo | Ruta / detalle |
|------|-----------------|
| Entidad | `entidades/SalaTransmision.java` (profesor_id, token, pin_sala_hash, pizarra_id) |
| Entidad | `entidades/PizarraTrabajo.java` (profesor_id, pizarra_id) |
| Repo | `repositorios/SalaTransmisionRepository.java` |
| Repo | `repositorios/PizarraTrabajoRepository.java` |
| Servicio | `PizarraService`: getOrCreatePizarraTrabajo, clonarPizarra, getOrCreateSalaTransmision, findSalaTransmisionByProfesor, setPizarraYPinSala, rotarTokenSala, guardarComoNuevaPizarra; construirEstadoParaSala y PIN resuelven por SalaTransmision primero |
| Controlador | `PizarraController`: GET `/profesor/pizarra` → redirect panel; GET `/profesor/pizarra/panel`; GET `/profesor/pizarra/lista`; POST `/profesor/pizarra/transmitir`; POST `/profesor/pizarra/transmitir/nuevo-enlace`; POST `/profesor/pizarra/clonar`; POST `/profesor/pizarra/guardar-como-nueva`; eliminar redirige a lista |
| Config | `CustomAuthenticationSuccessHandler`: inyección de `PizarraService` y llamada a `rotarTokenSala(profesor.getId())` tras login exitoso de profesor |
| Template | `pizarra-editor.html`: botón Actualizar en TV, Transmitir (API global), Insertar pizarra existente, Guardar (modal "Guardar como nueva"), modal Transmitir con enlace/código ya configurado y "Cambiar enlace" |
| Template | `pizarra-lista.html`: enlace "Ir al panel de pizarra" a `/profesor/pizarra/panel`; lista en `/profesor/pizarra/lista` |
| Template | `sala.html`: sin botón Actualizar; sin polling; meta no-cache; contenido solo al cargar la página |

### 12.9 Rutas actualizadas (resumen)

| Ruta | Descripción |
|------|-------------|
| `GET /profesor/pizarra` | Redirige a `/profesor/pizarra/panel` |
| `GET /profesor/pizarra/panel` | Obtiene o crea pizarra de trabajo, redirige a `editar/{id}` |
| `GET /profesor/pizarra/lista` | Lista de pizarras guardadas (administrar) |
| `POST /profesor/pizarra/transmitir` | Asigna pizarra actual a la sala del profesor; body: `pizarraId`, `pin`; respuesta: `token` |
| `POST /profesor/pizarra/transmitir/nuevo-enlace` | Genera nuevo token para la sala; body: `pizarraId` |
| `POST /profesor/pizarra/clonar` | Clona contenido de una pizarra en otra; body: `pizarraOrigenId`, `pizarraDestinoId` |
| `POST /profesor/pizarra/guardar-como-nueva` | Crea nueva pizarra con nombre indicado (copia del contenido actual); body: `pizarraId`, `nombre` |

---

## 13. Correcciones: panel tras borrar pizarra, guardar como nueva y listado al día (Feb 2026)

Esta sección documenta las correcciones realizadas tras el uso del panel: error 500 al volver tras borrar la pizarra de trabajo, validación al guardar como nueva y listado actualizado en "Insertar pizarra existente".

### 13.1 Error 500 al volver al panel tras borrar la pizarra de trabajo

- **Problema:** Si el profesor iba a "Administrar pizarras", borraba una pizarra que era la **pizarra de trabajo** actual y luego volvía al panel (`/profesor/pizarra/panel`), la aplicación respondía **500 Internal Server Error** con `DataIntegrityViolationException`: *Duplicate entry '1' for key 'pizarra_trabajo.UKc5tgn2c7io9bb8gjek40hf9y6'* en un `INSERT` a `pizarra_trabajo`.
- **Causa:**  
  - La pizarra borrada seguía referenciada en `pizarra_trabajo`.  
  - En `getOrCreatePizarraTrabajo` se detectaba que la pizarra ya no existía, se hacía `delete(pt.get())` y luego `crearPizarraTrabajo(profesor)`. El `DELETE` no se ejecutaba (flush) antes del `INSERT`, y la restricción única por `profesor_id` fallaba.
- **Solución:**  
  - **En `PizarraService.getOrCreatePizarraTrabajo`:** Después de `pizarraTrabajoRepository.delete(pt.get())` se llama a `pizarraTrabajoRepository.flush()` para que el DELETE se envíe a la BD antes de crear la nueva pizarra de trabajo.  
  - **En `PizarraService.eliminar`:** Antes de borrar la pizarra se elimina cualquier fila de `pizarra_trabajo` que apunte a esa `pizarra_id` (si el profesor tenía esa pizarra como de trabajo) y se hace `flush`. Así no quedan referencias huérfanas y al volver al panel no se intenta insertar un duplicado.
- **Comportamiento esperado:** Si no se encuentra la pizarra de trabajo (porque se borró), el sistema abre como **pizarra nueva**: se borra el registro viejo de `pizarra_trabajo`, se hace flush y se crea una nueva pizarra de trabajo.

### 13.2 Guardar como nueva: no crear si ya existe ese nombre

- **Problema:** Al guardar como nueva pizarra con un nombre que el profesor ya tenía en otra pizarra, se creaba una segunda pizarra con el mismo nombre, generando confusión en la lista.
- **Solución:** En `PizarraService.guardarComoNuevaPizarra` se valida que el profesor no tenga ya otra pizarra (distinta de la actual) con el mismo nombre (comparación sin distinguir mayúsculas). Si existe, se lanza `IllegalArgumentException` con el mensaje: *"Ya tenés una pizarra guardada con el nombre \"...\". Usá otro nombre."*  
  El controlador ya devuelve ese mensaje en la respuesta (400) y el front lo muestra en un `alert`.
- **Archivo:** `PizarraService.java` (uso de `pizarraRepository.findByProfesorIdOrderByFechaModificacionDesc` para comprobar nombres).

### 13.3 Insertar pizarra existente: listado siempre al día

- **Problema:** En el modal "Insertar pizarra existente" la lista de pizarras se generaba en el servidor al cargar la página del editor. Las pizarras **guardadas recientemente** en la misma sesión no aparecían hasta recargar la página.
- **Solución:**  
  - **Nuevo endpoint:** `GET /profesor/pizarra/api/listado`. Devuelve JSON con la lista de pizarras del profesor: `id`, `nombre`, `cantidadColumnas`.  
  - **Modal:** Al abrir "Insertar pizarra existente" se llama a este endpoint, se rellena la lista en el cliente (excluyendo la pizarra actual) y se muestra "Cargando listado..." mientras tanto. Así siempre se ven **todas** las pizarras del profesor, incluidas las guardadas en la misma sesión.
- **Archivos:**  
  - `PizarraController.java`: método `apiListado` con `@GetMapping("/api/listado")` y `@ResponseBody`.  
  - `pizarra-editor.html`: modal con contenedor vacío; al hacer clic en "Insertar pizarra existente" se abre el modal, se hace `fetch('/profesor/pizarra/api/listado')` y se construyen los enlaces en el DOM. Mensajes para "Cargando...", "No hay otras pizarras" y error de carga.

### 13.4 Resumen de archivos modificados en esta tanda

| Archivo | Cambio |
|---------|--------|
| `PizarraService.java` | `getOrCreatePizarraTrabajo`: `flush()` tras `delete` del registro de pizarra de trabajo cuando la pizarra ya no existe. `eliminar`: borrar y hacer `flush` de `PizarraTrabajo` que apunte a la pizarra antes de borrar la pizarra. `guardarComoNuevaPizarra`: validación de nombre duplicado (IllegalArgumentException). |
| `PizarraController.java` | `GET /profesor/pizarra/api/listado`: API que devuelve lista de pizarras del profesor (id, nombre, cantidadColumnas). Corrección de tipo en `getCantidadColumnas()` (int, no Integer). |
| `pizarra-editor.html` | Modal "Insertar pizarra existente": lista vacía al cargar; al abrir el modal se hace fetch a `/profesor/pizarra/api/listado` y se rellena la lista; mensajes de carga, sin pizarras y error. |

### 13.5 Ruta nueva

| Ruta | Descripción |
|------|-------------|
| `GET /profesor/pizarra/api/listado` | Listado de pizarras del profesor (id, nombre, cantidadColumnas) en JSON; usada por el modal "Insertar pizarra existente". |

---

## 14. Mejoras en el editor: reordenar ejercicios, copiar entre columnas, modal TV (Feb 2026)

Esta sección documenta las mejoras en el editor de pizarra: reorden de ejercicios dentro de cada columna (flechas y arrastre), arrastre entre columnas para copiar, aclaraciones en el modal "Transmitir en TV" y corrección del arrastre desde la lista de ejercicios.

### 14.1 Reordenar ejercicios dentro de cada columna

- **Funcionalidad:** En cada columna se puede cambiar el orden de los ejercicios de dos formas:
  - **Flechas:** Cada tarjeta de ejercicio tiene dos botones (subir / bajar) arriba a la derecha. Al hacer clic, el ejercicio se mueve un lugar y se persiste el orden en el servidor.
  - **Arrastrar:** Se puede arrastrar una tarjeta dentro de la misma columna y soltarla en la posición deseada; el resto de ejercicios se desplaza y se guarda el nuevo orden.
- **Backend:** Endpoint `POST /profesor/pizarra/reordenar-items` con body `columnaId` y `itemIds` (lista de ids de ítems en el orden deseado). Usa el método existente `PizarraService.reordenarItems(columnaId, itemIdsEnOrden, profesorId)`.
- **Frontend:** Tarjetas con `draggable="true"` y `data-exercise-id`; botones `.btn-subir-item` y `.btn-bajar-item`; en el drop dentro de la misma columna se calcula el índice de inserción, se reordena el DOM y se llama a `reordenar-items`. Función `reordenarColumnaDesdeDOM(dropZone, columnaId)` para sincronizar con el servidor.

### 14.2 Arrastrar entre columnas (copiar)

- **Funcionalidad:** Si se arrastra un ejercicio de una columna y se suelta en **otra columna**, se **copia** ahí (mismo ejercicio, mismo peso y repeticiones). El original permanece en su columna.
- **Implementación:** En el `dragstart` del ítem se guarda en `dataTransfer` (tipo `application/json`) el origen (`source: 'item'`), `exerciseId`, `columnaId`, `peso`, `reps`. En el `drop` de la columna, si el origen es ítem y la columna destino es distinta, se llama a `agregar-item` con la columna destino y esos datos; se crea una nueva tarjeta al final con los mismos valores. La tarjeta creada recibe `data-exercise-id` para poder arrastrarla de nuevo.

### 14.3 Modal "Transmitir en TV": sin código y texto aclaratorio

- **Problema:** No quedaba claro cómo usar el sistema "sin código" y el enlace "Sin código" parecía no hacer nada (solo vaciaba el campo, sin feedback).
- **Solución:**
  - **Texto del modal:** Se reemplazó el párrafo único por una lista clara: (1) Para que el TV abra sin pedir código: dejar el cuadro vacío o hacer clic en «Sin código» y luego *Guardar y ver enlace*. (2) Para pedir código de 4 dígitos: ingresar el código y luego *Guardar y ver enlace*.
  - **Campo:** Etiqueta "Código de 4 dígitos (opcional)" y placeholder "Vacío = el TV abre sin código".
  - **Enlace "Sin código":** Al hacer clic se vacía el campo y se muestra durante unos segundos el mensaje: "Campo vaciado. Guardá para que el TV abra sin código." Así se entiende que hay que guardar después.
  - **Mensaje cuando ya hay código:** Se añade la indicación de que para quitar el código se puede hacer clic en «Sin código» y guardar.

### 14.4 Arrastre desde la lista de ejercicios (fix)

- **Problema:** Tras implementar el arrastre entre columnas, dejó de funcionar arrastrar ejercicios desde el **panel de ejercicios** (lista izquierda) a las columnas.
- **Causa:** En las columnas se usaba `dropEffect = 'move'` en el `dragover`, mientras que el panel de ejercicios usa `effectAllowed = 'copy'`. En muchos navegadores esa combinación hace que el drop se rechace.
- **Solución:** En el `dragover` de la columna se usa siempre `dropEffect = 'copy'`. En el `dragstart` de los ítems de columna se cambió `effectAllowed` de `'move'` a `'copyMove'`, para que tanto el arrastre desde el panel (copy) como desde otra columna (copy/move) sean aceptados.

### 14.5 Resumen de archivos modificados

| Archivo | Cambio |
|---------|--------|
| `PizarraController.java` | Endpoint `POST /profesor/pizarra/reordenar-items` (body: `columnaId`, `itemIds`). |
| `pizarra-editor.html` | Tarjetas con flechas subir/bajar y `draggable`; `data-exercise-id`; `bindItemDrag` y lógica de drop para reorden (misma columna) y copia (otra columna); `reordenarColumnaDesdeDOM` y handlers de flechas; modal Transmitir en TV con texto aclaratorio, placeholder y feedback "Sin código"; `dropEffect = 'copy'` en columnas; `effectAllowed = 'copyMove'` en ítems. |

### 14.6 Ruta nueva

| Ruta | Descripción |
|------|-------------|
| `POST /profesor/pizarra/reordenar-items` | Reordena los ítems de una columna; body: `columnaId`, `itemIds` (array de ids en orden). |

---

## Sugerencia para commit (ej. commit 37)

**Título corto:**  
`feat(pizarra): panel de trabajo, conexión global TV, guardar como nueva`

**Descripción sugerida:**

```
- Entrada a Pizarra lleva al panel de trabajo (4 columnas), no a la lista.
- Conexión global: un enlace por profesor (SalaTransmision); nuevo token al login y opción "Cambiar enlace".
- Insertar pizarra existente: clonar contenido en la actual. Fix ObjectDeletedException al clonar.
- Vista TV sin botón Actualizar ni polling; actualización con F5. Botón "Actualizar en TV" en el panel guarda y avisa.
- Guardar crea nueva pizarra con nombre (no reemplaza); listado en /profesor/pizarra/lista.
- Documentación: FASE_7_PANTALLA_DE_SALA.md sección 12 con todos los cambios.
```

---

## Sugerencia para commit (correcciones panel y listado – ej. commit 38)

**Título corto:**  
`fix(pizarra): panel tras borrar pizarra de trabajo, validar nombre al guardar, listado al día`

**Descripción sugerida:**

```
- Fix 500 al volver al panel tras borrar la pizarra de trabajo: flush tras delete en pizarra_trabajo;
  al eliminar una pizarra se borra antes su fila en pizarra_trabajo si era la de trabajo.
- Guardar como nueva: no crear si ya existe una pizarra con ese nombre (mensaje al usuario).
- Insertar pizarra existente: listado por API (GET /profesor/pizarra/api/listado) al abrir el modal;
  se ven todas las pizarras, incluidas las guardadas recientemente.
- Documentación: FASE_7_PANTALLA_DE_SALA.md sección 13.
```

---

## Sugerencia para commit (reorden, copiar entre columnas, modal TV – ej. commit 39)

**Título corto:**  
`feat(pizarra): reordenar ejercicios en columnas, copiar entre columnas, aclarar modal TV`

**Descripción sugerida:**

```
- Reordenar ejercicios en cada columna: flechas subir/bajar y arrastre dentro de la columna.
  Endpoint POST /profesor/pizarra/reordenar-items (columnaId, itemIds).
- Arrastrar ejercicio de una columna a otra: copia (mismo ejercicio, peso y reps) sin quitar el original.
- Modal Transmitir en TV: texto más claro (sin código = campo vacío + Guardar; con código = ingresar 4 dígitos + Guardar).
  Enlace "Sin código" vacía el campo y muestra feedback; placeholder "Vacío = el TV abre sin código".
- Fix arrastre desde lista de ejercicios a columnas: dropEffect = 'copy' en columnas,
  effectAllowed = 'copyMove' en ítems para que funcione tanto desde panel como entre columnas.
- Documentación: FASE_7_PANTALLA_DE_SALA.md sección 14.
```

---

## 15. Vista TV – Tarjetas: nombre, peso, reps/tiempo y estilo (Feb 2026)

Mejoras en el diseño de las tarjetas de ejercicio en la vista TV (`sala.html`): orden de datos, formato compacto y estilo visual.

### 15.1 Nombre del ejercicio

- **Recuadro con puntas redondeadas:** El nombre va dentro de un recuadro con `border-radius: 10px`, fondo blanco semitransparente y borde sutil.
- **Texto justificado a la derecha:** `text-align: right` en `.item-nombre`.

### 15.2 Orden de datos en la tarjeta

- **1. Nombre** (arriba, en su recuadro).
- **2. Peso** (debajo del nombre): solo número + **K** (ej. `12K`), sin "kg".
- **3. Repeticiones o tiempo** (debajo del peso), formato corto con una letra:
  - Minutos: `12m`
  - Segundos: `15s`
  - Repeticiones: `25r`

### 15.3 Estilo de peso y reps/tiempo

- **Color:** Texto verde (`#7ee787`).
- **Fondo:** Sombreado negro (`background: rgba(0, 0, 0, 0.65)`), `border-radius: 6px`.
- **Contenedores compactos:** Los recuadros de peso y reps/tiempo tienen como máximo ~3 caracteres (2 cifras + letra). Se usa `width: fit-content` y `align-self: flex-end` para que el ancho se ajuste al contenido; padding reducido (`0.12rem 0.28rem`) para no dejar espacio sobrante.

### 15.4 Cambios técnicos en sala.html

- **HTML/Thymeleaf:** Estructura en columna: `.item-nombre` → `.item-peso` (si hay peso) → `.item-reps`. Sin fila lateral; peso y reps son bloques independientes.
- **Formato en servidor:** Peso: `item.peso + 'K'`. Reps/tiempo: según `unidad` → `repeticiones + 's'`, `+ 'm'` o `+ 'r'`.
- **JavaScript (actualizarDesdeAPI):** Misma estructura y formatos al redibujar desde `GET /sala/api/{token}/estado`.

### 15.5 Archivo modificado

| Archivo | Cambio |
|---------|--------|
| `templates/sala/sala.html` | Estilos: nombre con recuadro redondeado y text-align right; peso y reps en columna, verde con fondo oscuro, fit-content y padding reducido. HTML/Thymeleaf y JS con formato 12K, 12m, 15s, 25r. |

---

## 16. Vista TV: título arriba, contorno por columna, vueltas; login y sesión (Feb 2026)

Cambios en la vista TV (nombre como título, colores por columna, cantidad de vueltas), configuración de sesión y login personalizado, y correcciones en PizarraColumna/PizarraService.

### 16.1 Nombre del ejercicio como título (arriba de la tarjeta)

- **Antes:** El nombre del ejercicio iba dentro de la tarjeta (recuadro blanco a la derecha).
- **Ahora:** El nombre va **arriba de la tarjeta**, como título: texto **blanco sobre fondo negro** (`.item-titulo`), en una sola línea con ellipsis si es largo. La tarjeta (`.sala-item`) solo muestra imagen, peso y reps.
- **Estructura:** Cada ejercicio es un `.sala-item-wrap` con `.item-titulo` + `.sala-item` (imagen + `.item-datos` con peso y reps).

### 16.2 Sin espacio entre tarjetas y peso arriba a la derecha

- **Columnas:** En `.sala-columna-items` se puso `gap: 0` para quitar el espacio entre tarjetas dentro de cada columna.
- **Peso:** El dato de peso se muestra **arriba a la derecha** de la tarjeta; las repeticiones/tiempo siguen **abajo a la derecha** (`.item-datos` con `justify-content: space-between` y `margin-top: auto` en reps).

### 16.3 Contorno y punto de color por columna

- Cada columna tiene un **borde de color** (estilo “recuadro Excel”): columna 1 naranja, 2 verde, 3 amarillo, 4 azul, 5 fucsia, 6 cyan. Clases por `:nth-child(1)` a `:nth-child(6)`.
- En el **título de la columna** (nombre del usuario) se añadió un **punto** (círculo) del mismo color a la izquierda del texto (`.sala-columna-titulo-punto`), para identificar cada usuario por color.

### 16.4 Cantidad de vueltas por columna

- **Backend:** Campo `Integer vueltas` (nullable, 1–9) en `PizarraColumna`. En `PizarraEstadoDTO.ColumnaDTO` se añade `vueltas`. `PizarraService.actualizarBasico` acepta `List<Integer> vueltas` y persiste por columna; `construirEstadoDesdePizarra` incluye `vueltas` en cada columna del JSON. `PizarraController.actualizarBasico` lee `vueltas` del body.
- **Editor:** En el título de cada columna del panel de pizarra hay un **selector de vueltas** (opciones vacío “—” o 1 a 9). Al cambiar o al guardar nombre/títulos se envían también las vueltas en `actualizar-basico`. Si no se elige nada, no se muestra en la TV.
- **Vista TV:** En el contenedor del título de la columna, **a la derecha**, si la columna tiene vueltas (1–9) se muestra **“X Vueltas”** en **naranja** (`.sala-columna-titulo-vueltas`). Si no hay valor, no se muestra.

### 16.5 Sesión: tiempo de expiración

- En `application.properties` se usa **`server.servlet.session.timeout=30m`** (antes `spring.session.timeout=30m`) para que el cierre de sesión por inactividad lo gestione correctamente el servidor embebido (30 minutos sin actividad).

### 16.6 Login: plantilla personalizada siempre

- **Problema:** En `/login` a veces se mostraba la página por defecto de Spring Security (“Please sign in”) en lugar de la plantilla “Iniciar Sesión”.
- **Solución:** Se creó **`config/WebMvcConfig.java`** con `addViewController("/login").setViewName("login")` para que GET `/login` resuelva siempre a la vista `login`. Se eliminó el método `login()` de `PortalControlador` para evitar dos manejadores para la misma ruta.

### 16.7 Correcciones PizarraColumna y PizarraService

- **PizarraColumna:** Se quitaron las anotaciones Lombok `@Getter`/`@Setter` del campo `vueltas` y se añadieron **getter y setter manuales** (`getVueltas()`, `setVueltas(Integer)`), para que el proyecto compile aunque Lombok no procese la clase en el IDE.
- **PizarraService:** Se eliminó el import no usado `java.util.ArrayList`. Antes de `columnaRepository.saveAll(cols)` se añadió la condición `if (cols != null && !cols.isEmpty())` para evitar avisos de null-safety.

### 16.8 Archivos tocados

| Archivo | Cambio |
|---------|--------|
| `templates/sala/sala.html` | Nombre como título arriba (blanco/negro); gap 0; peso arriba a la derecha; contorno y punto de color por columna; “X Vueltas” en naranja a la derecha del título. |
| `templates/profesor/pizarra-editor.html` | Selector de vueltas (1–9 o vacío) en el título de cada columna; envío de `vueltas` en actualizar-basico. |
| `entidades/PizarraColumna.java` | Campo `vueltas` (Integer); getter/setter manuales (sin Lombok para vueltas). |
| `dto/PizarraEstadoDTO.java` | `ColumnaDTO.vueltas`. |
| `servicios/PizarraService.java` | `actualizarBasico` con `vueltas`; `construirEstadoDesdePizarra` con `colDto.setVueltas`; condición antes de `saveAll(cols)`; import ArrayList eliminado. |
| `controladores/PizarraController.java` | `actualizarBasico`: parseo de `vueltas` del body. |
| `config/WebMvcConfig.java` | Nuevo: ViewController GET `/login` → vista `login`. |
| `controladores/PortalControlador.java` | Eliminado método `login()`. |
| `resources/application.properties` | `server.servlet.session.timeout=30m`. |

### 16.9 Sugerencia de commit

**Título corto:**  
`feat(pizarra): título arriba en TV, colores por columna, vueltas; login y sesión`

**Descripción sugerida:** ver archivo `Documentacion/COMMIT_MSG_PIZARRA_VUELTAS_LOGIN_FEB2026.txt`.
