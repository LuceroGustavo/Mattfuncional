# Contexto del proyecto – Leé esto primero

**Uso:** Si trabajás desde otra PC (o abrís el repo de nuevo), leé este archivo primero y después los que necesites. Sirve para que vos o la IA de Cursor tengan contexto rápido del proyecto.

---

## 1. Qué es este proyecto

- **Nombre:** Mattfuncional (evolución de MiGym).
- **Qué hace:** App para un profesor/entrenador: gestiona **alumnos** (ficha, sin login), **ejercicios**, **series**, **rutinas** (asignación por alumno, enlace público por token), **calendario semanal** con asistencia (presente/ausente) y **progreso** (modal en ficha del alumno).
- **Quién usa:** Un único rol con acceso: el profesor (admin del sistema). No hay panel alumno ni panel admin separado.
- **Stack:** Spring Boot, Thymeleaf, MySQL, Bootstrap. Código en `src/main/java/com/mattfuncional/` (controladores, servicios, entidades, repositorios, config, dto, enums).

---

## 2. Dónde está cada cosa en el código

| Área | Dónde mirar |
|------|-------------|
| **Panel profesor** | `controladores/ProfesorController.java`, templates `profesor/*.html` |
| **Ejercicios** | `Exercise`, `ExerciseService`, `ExerciseCargaDefaultOptimizado`, `ExerciseRepository`, `ExerciseController` |
| **Series** | `Serie`, `SerieEjercicio`, `SerieService`, `SerieController`, `SerieRepository` |
| **Rutinas** | `Rutina`, `RutinaService`, `RutinaControlador`, `RutinaRepository`; hoja pública en `RutinaControlador` + template por token |
| **Alumnos (usuarios)** | `Usuario`, `UsuarioService`, `UsuarioRepository`; ficha en `ProfesorController` + `alumno-detalle.html` |
| **Calendario y asistencia** | `CalendarioController`, `CalendarioService`, `AsistenciaService`, `Asistencia`, `DiaHorarioAsistencia`, `SlotConfig`; template `semanal-profesor.html` |
| **Grupos musculares** | `GrupoMuscular`, `GrupoMuscularService`, rutas en `ProfesorController` (`/profesor/mis-grupos-musculares`) |
| **Seguridad** | `SecurityConfig.java`, `CustomAuthenticationSuccessHandler` |

---

## 3. Estructura de la documentación (todo en `Documentacion/`)

**Orden sugerido para ponerte en contexto:**

1. **Este archivo** (`LEEME_PRIMERO.md`) – Qué es el proyecto y dónde está cada doc.
2. **AVANCES_DEL_APP.md** – Todo lo que ya está implementado (resumen por módulo).
3. **AYUDA_MEMORIA.md** – Lista de mejoras: ítem por ítem, estado "por implementar" o "implementado".
4. **PLAN_DE_DESARROLLO_UNIFICADO.md** – Visión, fases, lo que se elimina, checklist, pendientes.
5. **ESTADO-PLANES-Y-PENDIENTES.md** – Qué falta priorizar y desfases entre docs (por si AYUDA_MEMORIA no está al día).

**Resto de documentos (consultar cuando haga falta):**

| Archivo | Para qué sirve |
|---------|-----------------|
| **CHANGELOG_UNIFICADO_FEB2026.md** | Detalle técnico de cambios de Feb 2026 (rutinas, series, progreso, ejercicios, grupos, alumno inactivo). |
| **CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md** | Detalle de asistencia en calendario y columna "Presente" en Mis Alumnos. |
| **ESTADO-PLANES-Y-PENDIENTES.md** | Qué falta priorizar y desfases entre docs (por si AYUDA_MEMORIA no está al día). |
| **PLAN_GRUPOS_MUSCULARES_ENTIDAD.md** | Diseño e implementación de grupos musculares como entidad (ya hecho). |
| **OPTIMIZACION_EJERCICIOS_PREDETERMINADOS.md** | Cómo se cargan y optimizan los ejercicios predeterminados (imágenes 1–60). |
| **RESUMEN_HISTORIAL.md** | Resumen del historial MiGym → Mattfuncional y decisiones de arquitectura. |

**En la raíz del proyecto:**

- **CHANGELOG.md** – Historial general de la app (versiones y cambios anteriores).
- **tarea_actual.md** – Si existe: estado de la tarea actual para commit (puede estar desactualizado).

---

## 4. Resumen rápido de estado (para la IA)

- **Implementado:** Ejercicios predeterminados (auto-asegurar 60), ABM ejercicios/series/rutinas, grupos musculares como entidad, asignación rutina→alumno, enlace público por token, hoja `/rutinas/hoja/{token}` (permitAll), Copiar enlace y WhatsApp desde ficha, **calendario y presentismo (cerrado por ahora):** calendario semanal con tres estados (pendiente/presente/ausente) por defecto pendiente sin ausente automático, excepciones por día/hora, columna Presente en Mis Alumnos con ciclo de 3 estados, historial y modal “Resumen mensual” sincronizados con el calendario vía API, calendario abre en nueva pestaña desde el panel; modal de progreso unificado, alumno inactivo (acciones deshabilitadas), filtros alumnos persistentes, orden de series y ejercicios.
- **Pendiente (próximos pasos):** Clic en alumno en calendario → ficha; día por excepción (recuperar clase); opcional cron para ausentes; Fase 5–8 (acceso anónimo reforzado, alumnos sin login, pantalla sala, página pública); mejorar ficha alumno.

---

## 5. Frase para dar contexto a la IA desde otra PC

Podés decirle a Cursor algo como:

- *"Leé `Documentacion/LEEME_PRIMERO.md` y después `Documentacion/AVANCES_DEL_APP.md` para tener contexto del proyecto."*
- O: *"Para entender el proyecto leé la documentación: empezá por Documentacion/LEEME_PRIMERO.md."*

Con eso la IA sabe qué es la app, dónde está el código y en qué doc profundizar según la tarea.

---

*Última actualización: Febrero 2026.*
