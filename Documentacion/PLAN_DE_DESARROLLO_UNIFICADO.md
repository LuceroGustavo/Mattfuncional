# Plan de desarrollo unificado – Mattfuncional

**Para contexto del proyecto (sobre todo desde otra PC):** [LEEME_PRIMERO.md](LEEME_PRIMERO.md).

**Última actualización:** Febrero 2026  
**Origen:** Fusión de los antiguos PLAN_DE_DESARROLLO y PLAN_MODIFICACIONES (ya eliminados); referencias a PLAN_GRUPOS_MUSCULARES_ENTIDAD.md.  
**Uso:** Un solo documento con la visión del proyecto, fases, lo que se elimina, lo que falta y checklist. Los avances implementados están en [AVANCES_DEL_APP.md](AVANCES_DEL_APP.md). Las tareas concretas por implementar están en [AYUDA_MEMORIA.md](AYUDA_MEMORIA.md).

---

## 1. Visión general

| Aspecto | Definición |
|--------|------------|
| **Nombre** | Mattfuncional (evolución de MiGym) |
| **Referencia** | Presupuesto - Mat Funcional.pdf + indicaciones del cliente |
| **Único rol con acceso** | Admin (el profesor administrador del sistema) |
| **Alumnos** | Solo ficha física + online, **sin usuario ni contraseña**; no hay login de alumno |
| **Acceso a rutinas** | Por enlace (ej. WhatsApp); el alumno abre el link y ve la rutina en HTML |
| **Objetivo** | Un único panel de profesor, sin panel alumno ni admin, con pantalla de sala y página pública |

---

## 2. Lo que QUEDA (reutilizar/adaptar)

- **Ejercicios:** ABM en panel profesor; predeterminados auto-asegurados desde `uploads/ejercicios/` (1–60). Estructura: `Exercise`, `ExerciseService`, `ExerciseCargaDefaultOptimizado`, `ExerciseRepository`.
- **Series:** ABM de series (agrupaciones de ejercicios). `Serie`, `SerieEjercicio`, `SerieService`, `SerieController`, `SerieRepository`.
- **Rutinas:** Creación en base a series, ABM, asignación rutina → alumno, enlace único (token), hoja `/rutinas/hoja/{token}`, Copiar enlace, WhatsApp desde ficha alumno.
- **Usuarios (alumnos):** Ficha (datos físicos y online). Pendiente: quitar usuario/contraseña del alumno (Fase 6).
- **Calendario semanal:** Mantener. Uso: alumnos que asisten, vista por día/horario, presentismo. Asistencia (presente/ausente/pendiente) ya implementada en calendario y vista Mis Alumnos. Estructura: `CalendarioController`, `CalendarioService`, `Asistencia`, `DiaHorarioAsistencia`, `SlotConfig`, `semanal-profesor.html`.
- **Grupos musculares:** Entidad `GrupoMuscular` (ya implementado). ABM en `/profesor/mis-grupos-musculares`. Ver AVANCES_DEL_APP.md y PLAN_GRUPOS_MUSCULARES_ENTIDAD.md.

---

## 3. Lo que NO QUEDA (eliminar o desactivar)

| Elemento | Acción recomendada |
|----------|---------------------|
| **Panel del usuario/alumno** | Eliminar rutas, controladores y vistas del panel alumno (`/usuario/*`, dashboard alumno con login). |
| **Chat profesor–alumno** | Eliminar módulo de mensajería: `MensajeController`, `MensajeService`, entidad `Mensaje`, repositorio, vistas de chat y referencias en navbar. |
| **Panel de administrador** | Eliminar `AdministradorController` y vistas bajo `admin/`. El profesor es el único rol. |
| **Creación de profesores** | Eliminar alta/edición de profesores. Un único profesor creado por el sistema (DataInitializer o config). |
| **Login de alumno** | Eliminar flujo de login para rol USER/alumno. |
| **WebSocket / chat en tiempo real** | Eliminar `WebSocketController`, `WebSocketConfig` y dependencias de WebSocket. |
| **Gestión de múltiples profesores** | Quitar lógica de “elegir profesor”, “profesores disponibles”, etc. |

### Archivos/carpetas a eliminar o dejar de usar

- `AdministradorController.java`, templates `admin/*`, `usuario/dashboard.html`, `profesor/chat-alumno.html`.
- `MensajeController`, `MensajeService`, `WebSocketController`, `WebSocketConfig`.
- Entidades/repos: `Mensaje`, `MensajeRepository`.
- Rutas y permisos en `SecurityConfig` y navbar relacionados con admin, alumno y chat.

---

## 4. Fases de implementación

| Fase | Contenido | Estado |
|------|-----------|--------|
| **Fase 1 – Limpieza** | Renombrar proyecto a Mattfuncional. Eliminar panel admin, chat, WebSocket, ABM de profesores. Ajustar SecurityConfig y navbar. | Completado |
| **Fase 2 – Un solo profesor** | Profesor único en arranque. Redirigir login al panel del profesor. Quitar referencias a admin y lista de profesores. | Completado |
| **Fase 3 – Ejercicios y series** | Ejercicios con asegurar predeterminados. ABM de ejercicios y ABM de series en panel profesor. | Completado |
| **Fase 4 – Rutinas y asignación** | ABM rutinas basadas en series, asignación rutina → alumno, token, hoja `/rutinas/hoja/{token}`, Copiar enlace, WhatsApp, modificar rutina con series, orden de series, tabs dashboard, logo. | Completado |
| **Fase 5 – Vista rutina por enlace** | Página pública (sin login) con token que muestre la rutina en HTML. Hoja en `/rutinas/hoja/{token}` implementada; opcional: reforzar permitAll en SecurityConfig. | Pendiente / parcial |
| **Fase 6 – Alumnos sin login** | Alumno solo como ficha (física + online), sin usuario/contraseña. Calendario semanal y presentismo (gran parte ya hecha). | Pendiente |
| **Fase 7 – Pantalla de sala** | Modo sala para TV: ruta de solo lectura, control desde panel profesor, vista fullscreen. Ruta tipo `/sala/{token}` o `/sala?token=xxx`. | Pendiente |
| **Fase 8 – Página pública** | Sitio institucional: presentación, servicios, horarios, contacto, promociones, productos. Rutas públicas bajo `/public` o raíz. | Pendiente |

---

## 5. Nuevos módulos a desarrollar

### 5.1 Pantalla de entrenamiento en sala (Fase 7)

- Mostrar rutinas/series/ejercicios en pantalla grande. Control solo desde el panel del profesor. Pantalla en sala sin interacción (solo visualización). Ruta con token; actualización por polling o similar.

### 5.2 Página pública del gimnasio (Fase 8)

- Presentación, servicios, horarios, formulario de contacto, promociones, productos. Contenido editable desde el panel (o fijo en una primera etapa).

---

## 6. Ajustes de modelo y seguridad

- **Roles:** Un solo rol efectivo (ADMIN). No hay panel admin separado; el ADMIN es el profesor.
- **Alumnos:** Solo registros de ficha; sin rol de login. Decidir si se mantiene `Usuario` para alumnos sin credenciales o se crea entidad `Alumno`.
- **Profesor único:** En DataInitializer crear un único usuario con rol ADMIN. Sin pantallas de crear/listar profesores.
- **Seguridad:** Rutas públicas para `/`, `/public/**`, `/rutinas/hoja/**`, `/sala/**`. Ruta privada: panel del profesor (ej. `/profesor/**`). Eliminar reglas para `/admin/**` y login alumno.

---

## 7. Checklist único

- [ ] Renombrar app a **Mattfuncional** (pom, títulos, documentación).
- [x] Un único **panel: profesor** (no admin, no alumno).
- [x] **Ejercicios:** Predeterminados desde `uploads/ejercicios/` (1–60); ABM en panel profesor.
- [x] **Series y rutinas:** ABM y rutinas basadas en series (crear, editar, orden de series).
- [ ] **Alumnos:** solo ficha (física + online), sin usuario/contraseña. **Backend OK; frontend pendiente**.
- [x] **Asignación de rutinas** + **enlace para WhatsApp** + **vista HTML de rutina** (hoja en `/rutinas/hoja/{token}`).
- [x] **Calendario semanal y presentismo** (dar presente/ausente desde calendario y vista Mis Alumnos; tres estados; mismo API).
- [ ] **Pantalla de entrenamiento en sala** (modo TV, control desde panel).
- [ ] **Página pública:** presentación, servicios, horarios, contacto, promociones, productos.
- [ ] **Eliminar:** panel alumno, chat, panel admin, creación de profesores, WebSocket, login alumno.

---

## 8. Pendientes inmediatos (próximas sesiones)

- **Ficha del detalle de alumno:** Mejorar vista de detalle (organización, legibilidad, accesos rápidos, historial y progreso más claros).
- **Calendario – Acceso al detalle del usuario:** **Resuelto (backend).** Pendiente de mejora visual al final.
- **Calendario – Día por excepción (recuperar clase):** **Resuelto (backend).** Pendiente de mejora visual al final.
- **Asistencia – Cron opcional:** **Resuelto (backend on-demand).** Cron nocturno opcional si se desea.

---

## 9. Documentos de referencia

| Documento | Contenido |
|----------|------------|
| [AVANCES_DEL_APP.md](AVANCES_DEL_APP.md) | Todo lo implementado hasta la fecha. |
| [AYUDA_MEMORIA.md](AYUDA_MEMORIA.md) | Lista de mejoras (ítem por ítem, estado implementado / por implementar). |
| [PLAN_GRUPOS_MUSCULARES_ENTIDAD.md](PLAN_GRUPOS_MUSCULARES_ENTIDAD.md) | Plan e implementación de grupos musculares (ya hecho). |
| [OPTIMIZACION_EJERCICIOS_PREDETERMINADOS.md](OPTIMIZACION_EJERCICIOS_PREDETERMINADOS.md) | Imágenes 1–60 en `uploads/ejercicios/`. |
| `Documentacion/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md` | Detalle técnico de asistencia en calendario y columna Presente. |

---

*Este plan unificado reemplaza el uso de los planes dispersos para la planificación. Se puede ir actualizando en cada iteración.*
