# Pendientes finales – Mattfuncional

**Última revisión:** Marzo 2026  
**Estado:** Casi todo resuelto. Quedan dos tareas principales.

---

## 1. Estructura actual de la documentación

Tras la reorganización, la documentación queda así:

| Archivo | Propósito |
|---------|-----------|
| **LEEME_PRIMERO.md** | Contexto del proyecto, acceso (URLs, credenciales), dónde está cada cosa en el código |
| **DOCUMENTACION_UNIFICADA.md** | Resúmenes: lo implementado, backup, Excel alumnos, despliegue, manual, referencias técnicas |
| **PLAN_DE_DESARROLLO_UNIFICADO.md** | Visión, fases, checklist, pendientes detallados (ítem por ítem) |
| **AYUDA_MEMORIA.md** | Lista rápida; redirige a PLAN_DE_DESARROLLO_UNIFICADO §9 |
| **servidor/DESPLIEGUE-SERVIDOR.md** | Despliegue en VPS (SSH, Nginx, menú, client_max_body_size) |

**En la raíz:** `CHANGELOG.md`, `README.md`, `tarea_actual.md`.

---

## 2. Referencia rota

- **ESTIMATIVO_RECURSOS_SERVIDOR.md** — El PLAN_DE_DESARROLLO_UNIFICADO lo menciona en las secciones de depuración anual, pero el archivo **no existe** (probablemente eliminado en la reorganización).
- **Acción:** Quitar la referencia o crear un archivo mínimo con criterios de depuración (qué conservar, plazos, etc.).

---

## 3. Pendientes principales (lo que falta)

### 3.1 Trabajo de excepciones (estilos de avisos/errores)

**Qué falta:** Dar estilos consistentes a las pantallas de excepciones, avisos y errores que muestra la aplicación.

**Dónde aparece:** PLAN_DE_DESARROLLO_UNIFICADO §9 "Después del backup (pendientes opcionales)" — ítem 1.

**Alcance sugerido:**
- Revisar pantallas que muestran errores (login, backup, importación, formularios).
- Unificar clases CSS para mensajes de error/aviso (ej. `.alert-danger`, `.alert-warning`).
- Revisar mensajes de validación y excepciones en controladores (evitar textos técnicos al usuario).
- Posibles ubicaciones: `backup.html`, `login.html`, formularios de alumno/ejercicio/serie/rutina, páginas de error.

**Estado:** Opcional; se puede abordar cuando corresponda.

**Progreso (verificado Feb 2026):**
- ✅ **Módulo Administración del sistema (HTML):** Todas las excepciones y mensajes de este módulo ya tienen estilo unificado (modales Mattfuncional, alertas `.alert-mattfuncional`). Incluye: usuarios-sistema, pagina-publica-admin, backup, depuracion, administracion.
- ⏳ **Pendiente:** Todo el resto de la aplicación (login, formularios de alumno/ejercicio/serie/rutina, calendario, pizarra, etc.).

---

### 3.2 Depuración anual de base de datos ✅ (implementado Feb 2026)

**Implementado:** Panel "Depuración de datos" en Administración (entre Sistema de backups y Manual de usuario). Dos tarjetas:
1. **Asistencias:** Elimina registros con fecha anterior a la elegida (el usuario selecciona la fecha).
2. **Rutinas asignadas:** Elimina rutinas asignadas a alumnos creadas antes de la fecha elegida. Las plantillas no se tocan.

Acceso: `/profesor/depuracion`. Servicio `DepuracionService`. Ver DOCUMENTACION_UNIFICADA.md §2.1.

---

## 4. Resumen ejecutivo

| Tarea | Prioridad | Esfuerzo estimado |
|-------|-----------|-------------------|
| Corregir referencia a ESTIMATIVO_RECURSOS_SERVIDOR | Baja | 5 min |
| Estilos de excepciones/avisos/errores | Opcional | 2–4 h |
| ~~Método de depuración de base~~ | ✅ Hecho | — |

---

*Documento creado tras revisión de la documentación reorganizada.*
