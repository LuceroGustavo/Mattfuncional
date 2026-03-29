# Plan de mejoras Mattfuncional desde MiGymVirtual (referencia)

**Objetivo:** Traer a **Mattfuncional** las mejoras de front y back que resultaron útiles en **MiGymVirtual** (app derivada, enfocada en clases virtuales y rutinas), sin romper lo que ya funciona y manteniendo la estructura del proyecto actual.

**Carpeta de referencia (solo lectura para comparar y copiar ideas):**

`APP referencia/Migymvirtual/`

(Paquete Java en referencia: `com.migimvirtual`; en Mattfuncional: `com.mattfuncional`. Las adaptaciones deben respetar nombres y rutas de Mattfuncional.)

---

## 1. Enfoque de trabajo

| Fase | Qué hacemos | Estado |
|------|-------------|--------|
| **A – Backend** | Modificaciones en Java, `application*.properties`, servicios, controladores, config, etc., tomando como guía la referencia. | En curso (empezamos por acá) |
| **Verificación** | Compilar, levantar app, probar flujos afectados. | Tras cada bloque de back |
| **B – Vistas** | Templates, CSS, JS, fragments; alineados con lo ya aplicado en back. | Después de validar A |

**Reglas:**

- Ir **de a poco**: un requerimiento o grupo pequeño por vez.
- **Referencia ≠ copia ciega:** solo incorporar lo que mejora Mattfuncional; no eliminar módulos de Mattfuncional (calendario, presentismo, pizarra, etc.) salvo que en un requerimiento explícito se pida.
- Mantener **convenciones** de Mattfuncional (nombres, seguridad, perfiles).

---

## 2. Contexto de MiGymVirtual (recordatorio)

- Misma base conceptual que Mattfuncional pero orientada a profesores **sin gimnasio físico**, clases virtuales.
- Énfasis en **creación de rutinas** y experiencia **100 % responsive**.
- En la referencia **no** están (o están acotados) calendario, presentismo, pizarra digital y otras piezas presenciales; en **Mattfuncional** esos módulos se mantienen salvo decisión futura explícita.

---

## 3. Requerimientos y seguimiento

Debajo se listan los pedidos. La columna **Estado** se actualiza a medida que se implementa.

| # | Área | Requerimiento | Estado |
|---|------|---------------|--------|
| 1 | Rutinas / Profesor | **Categorías como entidad** (ManyToMany con rutina): categorías de sistema por defecto (FUERZA, CARDIO, FLEXIBILIDAD, FUNCIONAL, HIIT), ABM «mis categorías», selección múltiple al crear/editar rutina, backup ZIP con `categorias.json` y restore compatible. | **Hecho** |

**Leyenda de estado:** `Pendiente` · `En progreso` · `Hecho` · `No aplica`

---

## 4. Bitácora breve (opcional)

| Fecha | Nota |
|-------|------|
| *Mar 2026* | Creado el plan: orden back → verificar → vistas. |
| *28 mar 2026* | Cerrado requisito #1: categorías como entidad alineadas con MiGymVirtual (entidad, servicio, repo, rutinas, vistas crear/editar, `/profesor/mis-categorias`, DataInitializer, ZIP import/export). |

---

## 5. Documentos relacionados

- [BASE_PARA_APP_VIRTUAL.md](BASE_PARA_APP_VIRTUAL.md) — Contexto del fork MiGymVirtual (dirección opuesta a este plan).
- [DOCUMENTACION_UNIFICADA.md](DOCUMENTACION_UNIFICADA.md) — Estado actual de Mattfuncional.
- [LEEME_PRIMERO.md](LEEME_PRIMERO.md) — Dónde está cada cosa en el código.

---

*Cuando pidas un cambio concreto, se agrega una fila en §3 y al cerrarlo se marca **Hecho** y se anota en §4 si hace falta.*
