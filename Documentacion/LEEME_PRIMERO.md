# Contexto del proyecto – Leé esto primero

**Uso:** Si trabajás desde otra PC (o abrís el repo de nuevo), leé este archivo primero y después los que necesites. Sirve para que vos o la IA de Cursor tengan contexto rápido del proyecto.

---

## 1. Qué es este proyecto

- **Nombre:** Mattfuncional (evolución de MiGym).
- **Qué hace:** App para un profesor/entrenador: gestiona **alumnos** (ficha, sin login), **ejercicios**, **series**, **rutinas** (asignación por alumno, enlace público por token), **calendario semanal** con asistencia (presente/ausente) y **progreso** (modal en ficha del alumno).
- **Quién usa:** Roles **DEVELOPER** (super admin), **ADMIN** y **AYUDANTE**. No hay panel alumno ni panel admin separado.
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
2. **MANUAL-USUARIO.md** – **Manual del usuario:** cómo usar la app (login, panel, alumnos, ejercicios, series, rutinas, calendario, asistencia, pizarra, usuarios del sistema). Para el profesor o administrador que usa el sistema a diario.
3. **AVANCES_DEL_APP.md** – Todo lo que ya está implementado (resumen por módulo).
4. **PLAN_DE_DESARROLLO_UNIFICADO.md** – Visión, fases, checklist y pendientes detallados (ítem por ítem). Unifica el plan de desarrollo y la ayuda memoria.
5. **ESTADO-PLANES-Y-PENDIENTES.md** – Qué falta priorizar y desfases entre docs.

**Resto de documentos (consultar cuando haga falta):**

| Archivo | Para qué sirve |
|---------|-----------------|
| **MANUAL-USUARIO.md** | Manual para el usuario final (profesor/admin): cómo hacer cada tarea en la app. |
| **PLAN_DE_DESARROLLO_UNIFICADO.md** | Visión, fases, checklist y pendientes detallados (unifica plan y ayuda memoria). |
| **CHANGELOG_UNIFICADO_FEB2026.md** | Detalle técnico de cambios de Feb 2026 (rutinas, series, progreso, ejercicios, grupos, alumno inactivo). |
| **CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md** | Detalle de asistencia en calendario y columna "Presente" en Mis Alumnos. |
| **ESTADO-PLANES-Y-PENDIENTES.md** | Qué falta priorizar y desfases entre docs. |
| **PLAN_GRUPOS_MUSCULARES_ENTIDAD.md** | Diseño e implementación de grupos musculares como entidad (ya hecho). |
| **OPTIMIZACION_EJERCICIOS_PREDETERMINADOS.md** | Cómo se cargan y optimizan los ejercicios predeterminados (imágenes 1–60). |
| **RESUMEN_HISTORIAL.md** | Resumen del historial MiGym → Mattfuncional y decisiones de arquitectura. |

**En la raíz del proyecto:**

- **CHANGELOG.md** – Historial general de la app (versiones y cambios anteriores).
- **tarea_actual.md** – Si existe: estado de la tarea actual para commit (puede estar desactualizado).

---

## 4. Resumen rápido de estado (para la IA)

- **Implementado (la mayoría):** Ejercicios predeterminados (1–60), ABM ejercicios/series/rutinas, grupos musculares, asignación rutina→alumno, hoja `/rutinas/hoja/{token}`, Copiar enlace y WhatsApp; **calendario completo:** presente/ausente/pendiente, clic en alumno a ficha, día por excepción (recuperar clase), columna Presente, historial y modal “Resumen mensual” ; **pizarra y sala TV**; **página pública** (landing, Planes, consultas, administración); ficha alumno mejorada; usuarios del sistema, administración. Ver AVANCES_DEL_APP.md.
- **Pendiente (lo que realmente falta):** **Manual del usuario** (actualizar MANUAL-USUARIO.md); **gestión de backup** (backups y descargas en el panel); **depuración de datos antiguos** (eliminar/archivar asistencia o presente de años anteriores, conservar al menos 12 meses). Ver ESTADO-PLANES-Y-PENDIENTES.md.

---

## 5. Frase para dar contexto a la IA desde otra PC

Podés decirle a Cursor algo como:

- *"Leé `Documentacion/LEEME_PRIMERO.md` y después `Documentacion/AVANCES_DEL_APP.md` para tener contexto del proyecto."*
- O: *"Para entender el proyecto leé la documentación: empezá por Documentacion/LEEME_PRIMERO.md."*

Con eso la IA sabe qué es la app, dónde está el código y en qué doc profundizar según la tarea.

---

*Última actualización: Febrero 2026.*
