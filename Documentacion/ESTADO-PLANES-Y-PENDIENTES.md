# Dónde están los planes y qué falta

**Contexto:** Para entrar en tema del proyecto ver [LEEME_PRIMERO.md](LEEME_PRIMERO.md). Todo lo implementado está en [AVANCES_DEL_APP.md](AVANCES_DEL_APP.md).

**Estado general (Feb 2026):** La mayoría del desarrollo está **completada**: calendario (presente/ausente, clic en alumno → ficha, día por excepción), pizarra y pantalla de sala, página pública (landing, Planes, consultas), ficha alumno, ejercicios, series, rutinas, grupos musculares, administración de usuarios y página pública, etc. Ver [PLAN_DE_DESARROLLO_UNIFICADO.md](PLAN_DE_DESARROLLO_UNIFICADO.md) y [AVANCES_DEL_APP.md](AVANCES_DEL_APP.md).

---

## Estructura actual (reorganizada)

| Documento | Ruta | Contenido |
|----------|------|-----------|
| **Avances del app** | `Documentacion/AVANCES_DEL_APP.md` | **Todo lo implementado** en un solo archivo. |
| **Plan de desarrollo unificado** | `Documentacion/PLAN_DE_DESARROLLO_UNIFICADO.md` | Visión, fases, checklist y **pendientes reales** (manual, backup, depuración). |
| **Documentación asistencia** | `Documentacion/CAMBIOS-ASISTENCIA-CALENDARIO-Y-VISTA-ALUMNOS.md` | Detalle técnico de asistencia en calendario y columna Presente. |
| **Plan grupos musculares** | `Documentacion/PLAN_GRUPOS_MUSCULARES_ENTIDAD.md` | Grupos musculares como entidad (ya implementado; referencia). |

---

## Lo que ya está hecho

- **Calendario:** Presente/ausente/pendiente, clic en alumno → ficha, día por excepción (recuperar clase), vista Mis Alumnos unificada.
- **Pizarra y sala TV:** Pizarra digital, columnas editables, vista TV con polling, control desde panel.
- **Página pública:** Landing en `/`, página Planes, formulario de consulta, administración desde panel.
- **Ficha alumno:** Detalle mejorado (organización, historial, progreso, accesos rápidos).
- **Resto:** Ejercicios, series, rutinas, grupos musculares, alumnos sin login, hoja de rutina por token, usuarios del sistema, consultas recibidas, etc. Ver AVANCES_DEL_APP.md.
- **Manual del usuario:** Implementado. Página HTML de manual accesible desde el panel del profesor (botón "Manual del usuario" → `/profesor/manual`). Incluye todas las secciones: acceso, panel, alumnos, ejercicios, grupos musculares, series, rutinas, calendario, presentismo, progreso del alumno, pizarra en sala, usuarios del sistema, administración y página pública, resumen rápido. El archivo [MANUAL-USUARIO.md](MANUAL-USUARIO.md) sigue como referencia en documentación.

---

## Lo que falta (pendientes reales)

| Ítem | Descripción |
|------|-------------|
| **Gestión de backup** | En el panel de administración: sección de backups y descargas (listar, descargar, opcionalmente importar). |
| **Depuración / eliminar datos viejos** | Método o proceso para archivar o eliminar datos antiguos (ej. registros de asistencia/presente de años anteriores). Conservar al menos 12 meses. Ver [ESTIMATIVO_RECURSOS_SERVIDOR.md](ESTIMATIVO_RECURSOS_SERVIDOR.md). |

Opcional / operativo: script en servidor para `alter_consulta_email_nullable.sql`; script/menú para borrar base de datos (ya existe opción en menú).

---

## Resumen: prioridad de lo pendiente

1. **Gestión de backup** – Backups y descargas desde el panel de administración.
2. **Depuración de datos antiguos** – Eliminar o archivar presente/asistencia de años anteriores (mantener al menos 12 meses).
