# Contexto del proyecto – Leé esto primero

**Uso:** Si trabajás desde otra PC (o abrís el repo de nuevo), leé este archivo primero y después los que necesites. Sirve para que vos o la IA de Cursor tengan contexto rápido del proyecto.

---

## 0. Cómo ingresar (acceso rápido)

| Entorno | URL | Login |
|---------|-----|-------|
| **Producción (cliente)** | `https://mattfuncional.com.ar` (cuando DNS/Certbot estén listos) o `http://200.58.127.154` vía Nginx | `/login` — correo y contraseña |
| **Local** | http://localhost:8080 | `/login` — mismo flujo |
| **Referencia histórica** | detodoya.com.ar / VPS Donweb anterior | Ver [MIGRACION_SERVIDOR_CLIENTE.md](MIGRACION_SERVIDOR_CLIENTE.md) y [servidor/DESPLIEGUE-SERVIDOR.md](servidor/DESPLIEGUE-SERVIDOR.md) |

**Páginas públicas (sin login):** `/` (landing), `/planes`, `/publica`, `/demo`.

**Credenciales de desarrollo** (creadas por `DataInitializer` al arrancar):

| Usuario | Correo | Contraseña |
|---------|--------|------------|
| Profesor/Admin | profesor@mattfuncional.com | profesor |
| Developer | developer@mattfuncional.com | Qbasic.1977.mattfuncional |

Tras iniciar sesión se redirige al **Panel del profesor** (`/profesor/{id}`).

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

| Archivo | Para qué sirve |
|---------|-----------------|
| **LEEME_PRIMERO.md** (este) | Contexto del proyecto, acceso, dónde está cada cosa en el código. |
| **DOCUMENTACION_UNIFICADA.md** | Resúmenes: lo implementado, backup, Excel alumnos, despliegue, manual, referencias técnicas. |
| **PLAN_DE_DESARROLLO_UNIFICADO.md** | Visión, fases, checklist y pendientes (incluye validación manual del backup ZIP). |
| **PLAN_MODIFICACION_VISTAS.md** | Plan responsive / vistas (estado de fases y §4.2.1). |
| **MIGRACION_SERVIDOR_CLIENTE.md** | Migración al VPS del cliente; **Anexo A** con checklist y comandos de instalación. |
| **FORK_APP_VIRTUAL.md** | Solo si vas a derivar un proyecto tipo MiGymVirtual (app virtual, sin pizarra/asistencia presencial). |

**Carpeta `servidor/`:** Despliegue en VPS (SSH, Nginx, menú). Ver `servidor/DESPLIEGUE-SERVIDOR.md` y las plantillas `nginx-*.conf`.

**En la raíz del proyecto:** `CHANGELOG.md` – historial general de la app.

---

## 4. Resumen rápido de estado (para la IA)

- **Implementado:** Ejercicios, series, rutinas, grupos musculares, alumnos (sin login), calendario y asistencia, pizarra y sala TV, página pública, manual en `/profesor/manual`, sistema de backup en código (ZIP, JSON y Excel alumnos). **Alineación responsive** con referencia MiGymVirtual en el alcance del plan de vistas (panel, ficha alumno, series, rutinas, asignar rutina, login, ejercicios, grupos, etc.). Ver `DOCUMENTACION_UNIFICADA.md` y `PLAN_MODIFICACION_VISTAS.md` §4.2.1.
- **Próximo paso operativo (mar 2026):** **Probar a fondo** la **nueva funcionalidad de backup** (export/import ZIP, Suplantar, manifest v1.1, series, imágenes grandes) según receta en `DOCUMENTACION_UNIFICADA.md` §2 y `CHANGELOG.md` (entrada 2026-03-30).
- **Pendiente de proceso / backlog:** Depuración automática o anual de datos antiguos (p. ej. asistencias de más de 12 meses); paridad responsive opcional en **Pizarra** y **Calendario** (fases 2.2–2.3 del plan de vistas). Ver `PLAN_DE_DESARROLLO_UNIFICADO.md`.

---

## 5. Frase para dar contexto a la IA desde otra PC

- *"Leé Documentacion/LEEME_PRIMERO.md y Documentacion/DOCUMENTACION_UNIFICADA.md para tener contexto del proyecto."*
- O: *"Para entender el proyecto: Documentacion/LEEME_PRIMERO.md y después PLAN_DE_DESARROLLO_UNIFICADO.md."*

---

*Última actualización: abr 2026 — documentación unificada (menos archivos); producción cliente Dattaweb; backup pendiente de validación manual.*
