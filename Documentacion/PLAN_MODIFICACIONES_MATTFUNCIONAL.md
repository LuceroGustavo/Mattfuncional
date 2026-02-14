# Plan de modificaciones: MiGym → Mattfuncional

**Fecha:** Febrero 2025  
**Referencia:** Presupuesto - Mat Funcional.pdf + indicaciones del cliente  
**Objetivo:** Transformar la app actual (MiGym) en **Mattfuncional**: un único panel de profesor, sin panel alumno ni admin, con pantalla de sala y página pública.

---

## 1. Visión general del nuevo sistema

| Aspecto | Definición |
|--------|------------|
| **Nombre** | Mattfuncional |
| **Único rol con acceso** | Admin (el profesor administrador del sistema) |
| **Alumnos** | Solo ficha física + online, **sin usuario ni contraseña**; no hay login de alumno |
| **Acceso a rutinas** | Por enlace (ej. enviado por WhatsApp); el alumno abre el link y ve la rutina en HTML |

---

## 2. Lo que QUEDA (reutilizar/adaptar)

### 2.1 Ejercicios
- **Carga de ejercicios predeterminados:** Mantener lógica actual pero **sin panel dedicado**. Un **botón en el panel de ejercicios** (ej. “Cargar predeterminados”) que ejecute la carga optimizada.
- **ABM de ejercicios:** Alta, baja y modificación de ejercicios (vista lista + formularios). Incluir imágenes/GIF/video si ya está en el modelo.
- **Estructura actual a aprovechar:** `Exercise`, `ExerciseService`, `ExerciseCargaDefaultOptimizado`, `ExerciseRepository`, templates de lista y formulario de ejercicios.

### 2.2 Series
- **ABM de series:** Crear, editar y eliminar series (agrupaciones de ejercicios).
- **Estructura:** `Serie`, `SerieEjercicio`, `SerieService`, `SerieController`, `SerieRepository`, templates `series/crearSerie.html` y lógica asociada.

### 2.3 Rutinas
- **Creación de rutinas en base a series:** Las rutinas se arman a partir de las series ya creadas (ABM de rutinas).
- **Estructura:** `Rutina`, `RutinaService`, `RutinaControlador`, `RutinaRepository`, templates `rutinas/crearRutina.html`, `editarRutina.html`, `verRutina.html`.

### 2.4 Usuarios (alumnos)
- **Creación de usuario/alumno:** Solo **ficha** (datos físicos y online).
- **Sin usuario ni contraseña** para el alumno: no existe login de alumno.
- **Estructura a adaptar:** `Usuario` (o entidad “Alumno” si se decide separar) sin credenciales de login; solo datos de ficha y relación con rutinas asignadas.

### 2.5 Asignación de rutinas y envío por WhatsApp
- **Asignar rutinas a usuarios** desde el panel del profesor.
- **Envío por WhatsApp:** Generar un **enlace único** por asignación (o por rutina + alumno). El profesor copia/envía el link por WhatsApp.
- **Vista para el alumno:** Al abrir el link, una **página HTML pública** (sin login) que muestre la rutina: ejercicios, series, imágenes/GIF/videos según lo definido en el presupuesto.
- **Estructura a aprovechar:** Lógica de asignación rutina–usuario; nuevo endpoint (o ruta pública) que, dado un token/enlace, devuelva el HTML de la rutina.

### 2.6 Calendario semanal
- **Mantener:** Calendario semanal (días y horarios).
- **Uso:** Carga de alumnos que asisten, vista por día/horario, presentismo.
- **Estructura:** `CalendarioController`, `CalendarioService`, `Asistencia`, `DiaHorarioAsistencia`, `SlotConfig`, templates `calendario/semanal.html` y `semanal-profesor.html`.

---

## 3. Lo que NO QUEDA (eliminar o desactivar)

| Elemento | Acción recomendada |
|----------|---------------------|
| **Panel del usuario/alumno** | Eliminar rutas, controladores y vistas del panel alumno (`/usuario/*`, dashboard alumno, rutinas del alumno con login). |
| **Chat profesor–alumno** | Eliminar módulo de mensajería: `MensajeController`, `MensajeService`, entidad `Mensaje`, repositorio, vistas de chat y referencias en navbar/menú. |
| **Panel de administrador** | Eliminar `AdministradorController` y todas las vistas bajo `admin/`. El profesor será el único rol y no debe existir “admin” como panel separado. |
| **Creación de profesores** | Eliminar alta/edición de profesores desde la app. **Un único profesor**, creado por el sistema (por ejemplo vía `DataInitializer` o script/config). No hay “lista de profesores” ni ABM de profesores. |
| **Login de alumno** | Eliminar flujo de login para rol USER/alumno; eliminar o simplificar `Usuario` para que no tenga usuario/contraseña cuando sea alumno. |
| **WebSocket / chat en tiempo real** | Eliminar `WebSocketController`, `WebSocketConfig` y cualquier dependencia de WebSocket. |
| **Gestión de múltiples profesores** | Quitar toda lógica de “elegir profesor”, “profesores disponibles”, asignación de ejercicios por profesor (en el nuevo modelo hay un solo profesor). |

### Archivos/carpetas a eliminar o dejar de usar (resumen)
- `AdministradorController.java`, `EjerciciosGestionController.java` (o fusionar solo la parte “cargar predeterminados” en el panel de ejercicios del profesor).
- Templates: `admin/*` (completo), `usuario/dashboard.html`, `profesor/chat-alumno.html`, vistas de login específicas de alumno.
- Servicios/controladores: `MensajeController`, `MensajeService`, `WebSocketController`, `WebSocketConfig`.
- Entidades/repos: `Mensaje` y `MensajeRepository` (si no se reutilizan para otra cosa).
- Rutas y permisos en `SecurityConfig` y navbar relacionados con admin, alumno y chat.

---

## 4. Nuevos módulos a desarrollar

### 4.1 Pantalla de entrenamiento en sala
- **Objetivo:** Modo para mostrar en **televisor/pantalla en el gimnasio** (reemplazo de pizarra).
- **Funcionalidad:**
  - Mostrar rutinas, series o ejercicios en pantalla grande.
  - Control **solo desde el panel del profesor** (elegir qué rutina/serie/día mostrar).
  - La pantalla en sala **no requiere interacción** (solo visualización).
- **Implementación sugerida:**
  - Ruta pública o con token simple, por ejemplo: `/sala/{token}` o `/sala?token=xxx`.
  - Página HTML fullscreen, tipografía grande, fondos contrastados.
  - El profesor desde su panel: “Enviar a pantalla de sala” → se genera/actualiza el enlace o se abre en una ventana secundaria que el cliente puede poner en el TV (o usar el mismo enlace en el navegador del TV).
- **Consideraciones:** Caché corta o actualización en tiempo real (polling cada X segundos) para que al cambiar desde el panel se actualice la pantalla.

### 4.2 Página pública del gimnasio
- **Objetivo:** Sitio institucional con dominio propio (el dominio lo contrata el cliente).
- **Secciones a incluir (según presupuesto):**
  - Presentación del gimnasio.
  - Servicios.
  - Horarios.
  - Formulario de contacto.
  - Publicación de promociones.
  - Espacio para productos (camisetas, snacks, servicios adicionales).
- **Implementación sugerida:**
  - Conjunto de rutas públicas bajo `/public` o `/` (raíz) que no requieran login.
  - Templates: `public/index.html`, `public/servicios.html`, `public/horarios.html`, `public/contacto.html`, `public/promociones.html`, `public/productos.html` (o una sola página con secciones ancla).
  - Formulario de contacto: POST a un endpoint que guarde en BD o envíe email (según lo acordado).
  - Contenido editable: idealmente desde el panel del profesor (CRUD de promociones, productos, textos) o, en una primera etapa, contenido fijo en HTML/Thymeleaf.

---

## 5. Ajustes de modelo y seguridad

### 5.1 Roles y acceso
- **Un solo rol efectivo:** ADMIN (único usuario que hace login).
- No hay panel admin separado: el ADMIN es el profesor que gestiona todo.
- Alumnos: solo registros de ficha, sin rol de login.

### 5.2 Usuario / Alumno
- Decidir si se mantiene la entidad `Usuario` para alumnos (sin `username`/`password`) o se crea entidad `Alumno` y se deja `Usuario` solo para el profesor.
- Relación: Alumno → Rutinas asignadas; para “ver mi rutina” solo mediante el enlace (token).

### 5.3 Profesor único
- En `DataInitializer` (o equivalente): crear un único usuario con rol ADMIN y credenciales por defecto o configuradas (cambiar en primer uso).
- No hay pantallas de “crear profesor” ni “listar profesores”.

### 5.4 Seguridad (Spring Security)
- Rutas públicas: `/`, `/public/**`, `/rutina/ver/**` (o la ruta del enlace de rutina), `/sala/**`.
- Una sola ruta privada: todo lo del panel del profesor (ej. `/profesor/**` o `/panel/**`).
- Eliminar reglas y referencias a `/admin/**` y a `/usuario/dashboard/**` (login alumno).

---

## 6. Orden sugerido de implementación

| Fase | Contenido |
|------|-----------|
| **Fase 1 – Limpieza** | Renombrar proyecto a Mattfuncional (nombre en pom, título en vistas). Eliminar panel admin, chat, WebSocket, ABM de profesores y lógica de múltiples profesores. Ajustar SecurityConfig y navbar. **Estado: completado** |
| **Fase 2 – Un solo profesor** | Configurar profesor único en arranque. Redirigir login exitoso al panel del profesor. Quitar referencias a “admin” y a “lista de profesores”. **Estado: completado** |
| **Fase 3 – Ejercicios y series** | Dejar un único flujo de ejercicios con botón “Cargar predeterminados”. Mantener ABM de ejercicios y ABM de series en el panel del profesor. **Estado: completado** |
| **Fase 4 – Rutinas y asignación** | Asegurar ABM de rutinas basadas en series. Asignación rutina → alumno. Generación de enlace único por asignación. **Estado: completado** |
| **Fase 5 – Vista rutina por enlace** | Página pública (sin login) que recibe token/enlace y muestra la rutina en HTML (ejercicios, series, imágenes). Preparar texto/enlace para compartir por WhatsApp. (Hoja implementada en `/rutinas/hoja/{token}`; opcional: permitir acceso anónimo en SecurityConfig.) |
| **Fase 6 – Alumnos sin login** | Ajustar modelo de alumno (ficha física + online, sin usuario/contraseña). Mantener calendario semanal y presentismo. |
| **Fase 7 – Pantalla de sala** | Desarrollo del modo “sala”: ruta de solo lectura, control desde panel del profesor, vista fullscreen para TV. |
| **Fase 8 – Página pública** | Desarrollo del sitio institucional: presentación, servicios, horarios, contacto, promociones, productos. |

---

## 7. Checklist rápido

- [ ] Renombrar app a **Mattfuncional** (pom, títulos, documentación).
- [x] Un único **panel: profesor** (no admin, no alumno).
- [x] **Ejercicios:** botón “Cargar predeterminados” en panel de ejercicios + ABM.
- [x] **Series y rutinas:** ABM y rutinas basadas en series.
- [ ] **Alumnos:** solo ficha (física + online), sin usuario/contraseña.
- [x] **Asignación de rutinas** + **enlace para WhatsApp** + **vista HTML de rutina** (Fase 4 completada: token, copiar enlace, WhatsApp, hoja de rutina, modificar rutina con series).
- [ ] **Calendario semanal** y presentismo.
- [ ] **Pantalla de entrenamiento en sala** (modo TV, control desde panel).
- [ ] **Página pública:** presentación, servicios, horarios, contacto, promociones, productos.
- [ ] Eliminar: panel alumno, chat, panel admin, creación de profesores, WebSocket, login alumno.

---

## 8. Avances recientes (Fase 4 – en progreso)

- **Enlace público con token:** la hoja de rutina se accede por `/rutinas/hoja/{token}`.
- **Copiar enlace:** botón en hoja de rutina y en ficha del alumno.
- **WhatsApp:** botón que abre chat con el **celular del alumno** y el enlace listo.
- **Asignar rutina:** tarjetas con botón **ver (ojo)**.
- **Vistas más ágiles:** rutinas asignadas del alumno en **tabla compacta** y sin estado “terminada/en proceso”.
- **Creación de rutinas:** botón **Crear serie** en selector de series.
- **Series:** botón **Ver serie** y nueva vista `series/verSerie.html`.

---

*Documento alineado con el presupuesto “Presupuesto - Mat Funcional.pdf” y con las indicaciones del cliente. Las fases pueden ajustarse según prioridad de entrega.*
