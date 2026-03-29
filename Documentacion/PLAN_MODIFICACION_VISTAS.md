# Plan de modificación de vistas (Mattfuncional → alineación con MiGymVirtual)

**Objetivo:** Unificar **aspecto responsive** y **paleta por módulo** con la app de referencia (`APP referencia/Migymvirtual/`), sin perder funcionalidad que **solo existe en Mattfuncional** (calendario, pizarra, presentismo, filtros por día/horario, etc.).

**Documentos de referencia en MiGymVirtual (leer antes de tocar CSS/HTML):**

| Documento | Contenido |
|-----------|-----------|
| `APP referencia/Migymvirtual/Documentacion/PALETA_COLORES.md` | Colores por módulo (hex y criterios pastel / botones). |
| `APP referencia/Migymvirtual/Documentacion/GUIA_RESPONSIVE.md` | Breakpoints, panel móvil, tablas con scroll, patrones por pestaña. |
| `APP referencia/Migymvirtual/Documentacion/DOCUMENTACION_UNIFICADA.md` | §0.1 paleta; detalles de administración y backups. |

**Código de referencia principal del panel:**  
`APP referencia/Migymvirtual/src/main/resources/templates/profesor/dashboard.html`  
(+ `fragments/navbar.html`, `footer`, `static/style.css` donde aplique).

---

## 1. Alcance y exclusiones

- **Sí:** Mismo criterio visual y responsive que MiGymVirtual donde haya equivalente funcional.
- **No (por ahora):** Forzar el mismo layout en módulos **que no existen** en la referencia si eso retrasa entrega; se busca **usable en móvil** y coherencia de marca, no clon pixel a pixel imposible.
- **Módulos solo Mattfuncional:** **Pizarra**, **Calendario** (vista semanal / APIs de asistencia en panel), **Presentismo** integrado en listados — se tratan en fases propias después de alinear el panel base.

---

## 2. Orden de trabajo acordado

| Fase | Vista / módulo | Notas |
|------|----------------|--------|
| **2.1** | **Panel del profesor** (`profesor/dashboard.html`) | Tarjetas superiores + barra de acciones + estructura móvil. |
| **2.2** | **Pizarra** | Responsivo razonable; colores propios si no hay analogía en referencia. |
| **2.3** | **Calendario** | Idem. |
| **2.4** | Pestañas del panel + **crear/modificar serie** + **crear rutina** + **hoja / ver rutina** (`/profesor/rutinas/ver/{id}`, `/rutinas/hoja/{token}`) | **Estado:** ver §4.2.1 (incl. hoja de rutina y **ver serie**). Siguiente: §2.2–2.3; **editar rutina** tabla/modal opcional. |

---

## 3. Fase 2.1 — Panel del profesor (primer entregable)

**Estado (Mar 2026):** Implementado en código — paleta de tarjetas secundarias (Ejercicios naranja, Admin gris, Pizarra ámbar distinto), iconos por módulo en tarjetas grandes, icono **Series** = `fa-layer-group`, contador de ejercicios en tarjeta, vista móvil con grid de 8 accesos (incl. Calendario y Pizarra), pestañas ocultas en móvil con navegación por tarjetas, `?tab=` en historial al cambiar pestaña.

---

### 3.1 Tarjetas grandes (contadores)

En la referencia, las cuatro tarjetas **Alumnos, Series, Rutinas, Asignaciones** comparten estructura (`card-module`, gradientes, iconos Font Awesome):

| Tarjeta | Fondo (referencia) | Icono (referencia, escritorio) |
|---------|-------------------|---------------------------------|
| Alumnos | `#c8e6c9` → `#b2dfdb` | `fa-users` |
| Series | `#e1bee7` → `#d1c4e9` | `fa-layer-group` |
| Rutinas | `#fff9c4` → `#ffe082` | `fa-list-check` |
| Asignaciones | `#bbdefb` → `#b3e5fc` | `fa-clipboard-list` |

**Mattfuncional** replica esos gradientes; iconos por módulo en escritorio y móvil aplicados (ver estado arriba).

### 3.2 Tarjetas rectangulares secundarias (`main-action-card`)

En **MiGymVirtual** el layout es: **Ejercicios** (naranja pastel `#ffccbc` / `#ffab91`, icono `#bf360c`, `fa-dumbbell`), **Administrar** (gris `#cfd8dc` / `#b0bec5`, `fa-cogs`). **No** hay pizarra ni calendario en el grid de la referencia.

En **Mattfuncional** (implementado): Ejercicios y Admin alineados a la referencia; Calendario en celeste con `fa-calendar-week`; Pizarra en ámbar más claro que Ejercicios e icono `fa-tv`.

### 3.3 Botones bajo el título (`main-actions`)

Alinear colores con la referencia (`btn-alumnos`, `btn-series`, `btn-rutinas`, `btn-calendario`, etc.) y tamaños táctiles en móvil (`GUIA_RESPONSIVE.md` §5).

### 3.4 Responsive del dashboard

Tomar de la referencia:

- Grid → apilado / ancho completo bajo **991px**.
- Bloque **dashboard-mobile-card** (si aún no está parity completa en Matt): mismas clases y comportamiento que `dashboard.html` referencia.
- `table-responsive` + clases de scroll (`mgv-scroll-panel` / equivalente en `style.css` de Matt).

**Archivos típicos a tocar:**  
`src/main/resources/templates/profesor/dashboard.html`, `static/style.css`, posiblemente `fragments/navbar.html` / `footer`.

---

## 4. Fase 2.4 — Pestañas: Alumnos, Series, Rutinas, Asignaciones

### 4.1 Modelo de color

Aplicar la misma familia de color por pestaña que en la referencia (cabeceras, botones “Crear / Nueva…”, botón **Limpiar** del filtro, badges):

- Alumnos → verdes (`#c8e6c9`, `#81c784`, texto `#1b5e20`).
- Series → violetas.
- Rutinas → amarillos / naranja oscuro para texto.
- Asignaciones → azules.

### 4.2 Filtros y tabla

- **Series / Rutinas / Asignaciones:** copiar de la referencia el patrón **card de filtro** bajo el título, placeholder + **Limpiar** en la misma fila, y mejoras de UX que ya estén en `dashboard.html` referencia.
- **Mis Alumnos (excepción explícita):**
  - **Conservar** el filtro actual que usa **días y horarios** (no existe igual en MiGymVirtual).
  - **Conservar** la columna / lógica de **presentismo** y enlaces a flujos de asistencia.
  - **Sí aplicar:** formatos de **botones**, **badges**, **card** del filtro donde no pisen la lógica de días/horarios, y responsive de tabla (popover móvil, columnas ocultas, etc.) según `GUIA_RESPONSIVE.md` §5 y §5.1.

### 4.2.1 Registro de implementación (entorno 1 / 2)

**Convención:** Ir documentando solo en **este archivo** (`PLAN_MODIFICACION_VISTAS.md`), ampliando §4.2.1 y el estado de fases, para no multiplicar documentos.

**Terminología:** Entorno 1 ≈ escritorio (≥992px); entorno 2 ≈ móvil (≤991px en panel). Referencia: `APP referencia/Migymvirtual/`.

#### Login (`templates/login.html`) — **actualizado (Mar 2026)**

- **Entorno 2 (≤991px):** fondo **`/img/login.png`** en capa fija `body.login-page::before` con **`background-size: contain`** para ver el arte completo en pantalla (relleno `#d0d0d0` si hay bandas); `background-position: center top`.
- **Escritorio (≥992px):** fondo **`/img/logo-Navbar.png`** con `cover` en `body`.
- **Tarjeta de login:** paleta **gris hormigón / carbón** (cabecera oscura, cuerpo claro, botón **Ingresar** `.btn-login-submit`); formulario posicionado con `clamp` en `padding-top` para equilibrio entre logo del PNG y pie de pantalla (ajustes finos iterados).
- `viewport`, `100dvh`, `safe-area`, padding responsive; toggle contraseña y `autocomplete` en campos; variables JS (`currentUserName`, `usuariosSistema`) escapadas para evitar roturas por comillas en el script de login.

#### Navbar (`templates/fragments/navbar.html`)

- Entorno 2: barra compacta tipo referencia (logo + marca, sobre consultas en recuadro, texto de rol, **Salir**); ocultos Volver, panel, bloque nombre/correo y avatar.
- Enlaces de marca según sesión/rol (`sec:authorize` + `th:if` / `th:unless`).

#### `static/style.css`

- ≤576px: `.modal-confirmar-footer` en columna y botones anchos (confirmaciones genéricas).
- Excepción `#modalVerSerieMobile`: pie en fila, botones proporcionados; colores pastel para acciones del modal serie.
- `.modal-card-rutina`: tarjeta del modal de detalle de rutina.
- `.table-responsive.mgv-scroll-panel` (≤991px): scroll interno en tablas de lectura del panel.
- **≤991px — `#modalProgreso`:** pie del modal *Registrar progreso* en **una fila**, botones compactos (no columna a ancho completo como el footer genérico).

#### Ficha **detalle de alumno** (`profesor/alumno-detalle.html`) — **completo (Mar 2026)**

- **Entorno 1 / 2:** `container-fluid` responsive; rejilla de tarjetas (Datos personales con ícono editar, Restricciones, Asistencia programada, **Progreso del alumno**, Rutinas asignadas); bloque complementario sin duplicar restricciones.
- **Sin “Volver al Dashboard”** en cabecera (navegación vía panel / barra inferior).
- **Escritorio (≥lg):** acciones **Progreso** + **Editar** en fila superior; **Eliminar usuario** al pie de la página.
- **Móvil:** progreso desde tarjeta (`#modalProgreso`); editar desde tarjeta Datos personales; presentismo con tabla compacta + **Consultar asistencias** (modal mensual JSON); filas de asistencia tocables → `#modalVerAsistenciaMobile` (delegación en `#historialAsistenciaBody`).
- **Rutinas (últimas 3):** columnas ocultas en entorno 2; fila → `#modalVerRutinaMobile` con rutas y acciones Mattfuncional; `mgv-scroll-panel` donde aplica.
- **Modales:** cabecera `modal-confirmar-header` (violeta) y pie `modal-confirmar-footer`; formulario de progreso con checklist en flex y botón **Guardar** `btn-primary`.
- **Footer de marca** (`footer.footer`) **oculto** en esta vista (`body.ficha-alumno-body`).
- **Nota de modelo:** el “progreso” se registra sobre **asistencia** (no tabla `registrosProgreso` separada como en la referencia); el historial corto refleja presentismo + trabajo + observaciones.

#### Panel del profesor — footer

- **`profesor/dashboard.html`:** mismo criterio; `body.panel-profesor-body` oculta `footer.footer`.

#### Pestaña **Mis Alumnos** (`profesor/dashboard.html`)

- Entorno 2: columnas visibles **Nombre, Edad, Estado, Presente**; resto oculto hasta `lg`.
- Clic en fila → detalle (`data-alumno-href` + delegación en `tbody`); no navega si el clic es en enlace o botón (p. ej. presentismo).
- Filtros móvil: Estado + Tipo en una fila; Día + Horario en otra.

#### Pestaña **Mis Series**

- Card filtro buscar + Limpiar violeta; `#tablaSeries`; datos en `data-*` por fila.
- Entorno 2: sin columna Acciones; fila abre `#modalVerSerieMobile`; Ver en móvil misma pestaña.

#### Vista **crear / modificar serie** (`series/crearSerie.html`) — **completo (Mar 2026)**

- **Misma plantilla** para alta (`GET/POST /series/crear`) y edición (`GET/POST /series/editar/{id}`); título y botón según `editMode`.
- **Entorno 1 / 2:** `crear-serie-container` con padding responsive; sección **Datos de la serie** (`form-section` gradiente + borde violeta); **Grupo muscular** con wrapper violeta en móvil; **Ejercicios de la serie** con card filtro (buscar + **Limpiar** `btn-series-filtro`), tabla `table-sm` en `table-responsive`, columnas **Orden** y **Acción** con clases `orden-accion-*` y botones **Subir/Bajar**; en móvil **tabla primero** (`order-1` / `order-2` en la fila del carrusel).
- **Carrusel Splide** de ejercicios (config tipo referencia: `perMove`, `speed`, `flickPower`, etc.); listener **Limpiar**; `actualizarOrdenAccionFilas()` en altas/bajas y al editar fila.
- **Modal de alerta** Bootstrap con `modal-confirmar-header` / `modal-confirmar-footer`; orden de scripts: Splide → Bootstrap → script de la página (para `bootstrap.Modal`).
- **Post-guardado:** campo opcional `volverUrl` en el form (`th:if`); si viene, el JS redirige allí; si no, a `/profesor/dashboard?tab=series`.
- **Sin footer de marca** en esta vista (como referencia MiGymVirtual); se mantiene **bottom-nav** del panel.

#### Vista **crear rutina** (`rutinas/crearRutina.html`) — **completo (Mar 2026)**

- **Alineación MiGymVirtual:** series disponibles en **tabla** (`#tablaSeriesCrear`: Agregar +, Nombre, N° ej., Descripción, Ver detalle), no en tarjetas; **buscador** por nombre; **modal detalle** (`#modalDetalleSerie`) con tarjeta pastel violeta (`.modal-card-serie`), badges de ejercicios, acciones **Ver serie**, **Modificar**, **Agregar**; lista **Series seleccionadas** con Vueltas, Subir/Bajar, **Eliminar** y **resaltado de filas** en tabla (`fila-serie-seleccionada` + `syncSelectedRows`).
- **Entorno 1 / 2:** `viewport`, `crear-rutina-container`; en móvil **Seleccionar series** primero (`order-1` / `order-2` respecto a datos de la rutina); leyenda corta en móvil para la lista ordenada; columna Descripción oculta en &lt;576px (`.col-descripcion-movil`).
- **Validación** en modal de alerta (`modal-confirmar-header` / `modal-confirmar-footer`). **Sin footer de marca**; **bottom-nav** del panel.
- **Datos:** `SerieRepository.findByProfesorIdWithSerieEjercicios` + servicio expuesto para poblar tabla/modal sin N+1; `RutinaControlador` GET crear y reintento tras error de categorías usan esa carga.
- **Navegación `volver`:** `SerieController` acepta `volver` en `GET /series/ver/{id}` y `GET /series/editar/{id}`; desde el modal, enlaces con `?volver=/rutinas/crear`. **`verSerie.html`:** barra **Volver** al valor de `volver` o **Volver al panel** a `dashboard?tab=series`. Edición de serie sigue usando `volverUrl` en `crearSerie.html` para regresar a crear rutina.
- **Nota:** **Modificar rutina** (`rutinas/editarRutina.html`) aún usa tarjetas para series nuevas; unificar a tabla+modal es trabajo aparte.

#### Vista **hoja de rutina** (`rutinas/verRutina.html`) — **completo (Mar 2026)**

- **Rutas:** `GET /profesor/rutinas/ver/{id}` (`ProfesorController`, vista privada profesor) y `GET /rutinas/hoja/{token}` (`RutinaControlador`, enlace alumno); **misma plantilla** Thymeleaf.
- **Modelo:** `esVistaEscritorio = false` también para el profesor (antes `true` forzaba 3 columnas en móvil y rompía la paridad con la hoja del alumno).
- **Cabecera** alineada a MiGymVirtual: primera fila **logo + marca** | **fecha**; segunda fila **nombre de la rutina** a ancho completo, centrado, verde `#7ee787`.
- **Bloques (series):** colores por índice con clases **`serie-bloque-0` … `serie-bloque-5`** (`th:classappend` con `serieStat.index % 6`), no `:nth-child` sobre el contenedor — así el primer bloque sigue naranja aunque exista **reseña del profesor** arriba. Borde, punto y texto **vueltas** comparten el color del bloque; nombre de serie y vueltas con **ellipsis** si hace falta.
- **Entorno 2 (≤991px):** ejercicios en **una sola columna**, **uno debajo del otro** (lista vertical como referencia; sin carrusel horizontal). Título de ejercicio hasta **2 líneas** (`line-clamp`); tarjetas a **ancho completo**; altura de área de imagen coherente con escritorio.
- **Entorno 1 (≥992px):** grid **3 columnas** para ejercicios (vista densa). Mantiene **modal zoom** al tocar tarjeta y **`th:if` exercise no nulo** en el ítem.
- **Contenedor:** `overflow-x: hidden` en `.hoja-container` para evitar scroll lateral accidental.

#### Vista **ver serie** (`series/verSerie.html`) — **actualizada (Mar 2026)**

- Sin `min-width` fijo en `body`; grid **1 / 2 / 3** columnas según breakpoint; media queries móvil para tipografías y alturas de tarjeta. **Vueltas** en cabecera en verde `#7ee787` (como referencia). **Volver** / **Volver al panel** con estilo oscuro; parámetro **`volver`** desde `SerieController` cuando aplica.

#### Pestaña **Mis Rutinas**

- **Categorías** + **Nueva Rutina**; card filtro (amarillo rutina) + Limpiar.
- Tabla sin columna ID; categorías como badges; entorno 2 sin descripción ni acciones en tabla → `#modalVerRutinaMobile` (`.modal-card-rutina`); Ver en móvil misma pestaña.
- Tabla con `mgv-scroll-panel` donde aplica.

#### Pestaña **Rutinas Asignadas**

- Cabecera solo con título; **card** con input “Filtrar por alumno…” + botón **Limpiar** (estilo celeste asignaciones).
- Tabla `#tablaAsignaciones` con `mgv-scroll-panel`: **Usuario, Rutina, Estado, Fecha, Acciones** (botones icónicos como referencia).
- Filas `fila-asignacion asignacion-row-clickable` y atributos `data-*` para modal (nombre alumno, fecha `dd/MM/yy` en modal, nombre rutina, categorías unidas, número de series, estado, reseña `notaParaAlumno`, token, URLs editar/ver/hoja/pausar/activar/eliminar).
- **Entorno 2:** columna **Acciones** oculta (`.col-asignaciones-acciones`); clic en fila (sin `a` ni `button`) abre **`#modalVerAsignacionMobile`** con tarjeta gradiente y acciones **Modificar, Ver, Copiar enlace, Pausar/Activar, Eliminar**; copiar enlace arma URL absoluta con `data-url-hoja` o token.

#### Pendiente (tras pestañas del panel)

- **2.2 / 2.3** Pizarra y Calendario responsive.

### 4.3 Detalle de alumno

**Estado: implementado y documentado en §4.2.1** (“Ficha detalle de alumno”). La referencia MiGymVirtual sirve como guía visual; Mattfuncional conserva presentismo, consulta mensual de asistencias, notas del profesor y rutas propias (`/rutinas/...`, `/profesor/alumnos/.../progreso`).

---

## 5. Seguimiento

- Ir cerrando **sub-bloques** (p. ej. “2.1 tarjetas escritorio”, “2.1 móvil”, “2.1 ejercicios+admin colores”) y compilar / probar en navegador real y tamaño móvil.
- **Documentación del plan de vistas:** actualizar **solo** este `PLAN_MODIFICACION_VISTAS.md` (estados de fase y §4.2.1), no crear archivos nuevos en `Documentacion/` salvo decisión explícita del proyecto.
- Opcional: añadir filas en `MEJORAS_DESDE_MIGYMVIRTUAL.md` por cada bloque de vistas completado.

---

## 6. Resumen ejecutivo

1. Leer **`PALETA_COLORES.md`** y **`GUIA_RESPONSIVE.md`** de la referencia.  
2. **Panel:** igualar colores e iconos de tarjetas equivalentes; corregir **Ejercicios** (naranja) y **Administrar** (gris); definir paleta explícita para **Calendario** y **Pizarra** sin confundir con otros módulos.  
3. **Luego** pizarra y calendario como vistas dedicadas.  
4. **Después** pestañas del panel con paleta MiGymVirtual y filtros mejorados, **salvo** filtro por días/horarios y presentismo en alumnos.

---

*Creado para Mattfuncional. Orden de trabajo acordado: panel profesor → pizarra → calendario → pestañas (con excepción alumnos).*
