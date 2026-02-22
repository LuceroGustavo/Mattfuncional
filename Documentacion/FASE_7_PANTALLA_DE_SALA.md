# Fase 7 – Pantalla de sala (Pizarra digital para TV)

**Para contexto del proyecto:** [LEEME_PRIMERO.md](LEEME_PRIMERO.md).  
**Plan de desarrollo:** [PLAN_DE_DESARROLLO_UNIFICADO.md](PLAN_DE_DESARROLLO_UNIFICADO.md).

**Estado:** En desarrollo (panel de trabajo y conexión global implementados)  
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
