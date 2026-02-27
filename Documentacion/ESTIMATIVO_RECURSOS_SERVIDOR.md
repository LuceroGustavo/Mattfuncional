# Estimativo de recursos – Mattfuncional en servidor

**Contexto:** Servidor con **2 vCPU / 2 GB RAM**, **20 GB contratados** (OS y otros ~5 GB), **25 GB SSD** total, **1000 GB** transferencia mensual.  
**Objetivo:** Estimar si el uso previsto de la app puede generar inconvenientes a futuro.

---

## 1. Escenario de uso (tus cifras)

| Dato | Cantidad | Notas |
|------|----------|--------|
| Alumnos | 150 | Fichas (sin login) |
| Alumnos con rutinas asignadas | ~35 | Virtuales que reciben rutinas |
| Series creadas | 50 | Series plantilla del profesor |
| Ejercicios | 100 | Incluye predeterminados |
| Rutinas | 50 | Plantillas y/o asignadas |
| Pizarras guardadas | 20 | Pizarras con columnas e ítems |

Relaciones aproximadas (para el cálculo):
- Cada **rutina** suele tener varias **series** (ej. 3–5). Si 50 rutinas tienen en promedio 4 series cada una → **~200 series** en total (plantilla + copias en rutinas).
- Cada **serie** tiene varios **ejercicios** (ej. 6–10). Promedio 7 → **~1.400 filas** en `serie_ejercicio`.
- Cada **pizarra** tiene 1–6 columnas (estimamos 4) y cada columna varios ítems (estimamos 8) → **20 × 4 × 8 ≈ 640 ítems** de pizarra.
- **Asistencia:** 150 alumnos × 2 asistencias/semana × 52 semanas ≈ **15.600 registros/año** (crece con el tiempo).

---

## 2. Almacenamiento (disco)

### 2.1 Base de datos MySQL

Estimación por tabla (tamaño aproximado por fila × cantidad):

| Tabla / concepto | Filas aprox. | Tamaño aprox. por fila | Total aprox. |
|------------------|-------------|-------------------------|--------------|
| usuario | 150 | ~1,5 KB | ~225 KB |
| exercise | 100 | ~0,8 KB | ~80 KB |
| imagen (solo metadata) | 100 | ~0,3 KB | ~30 KB |
| serie | 200 | ~0,2 KB | ~40 KB |
| serie_ejercicio | 1.400 | ~0,15 KB | ~210 KB |
| rutina | 50 | ~0,3 KB | ~15 KB |
| pizarra | 20 | ~0,2 KB | ~4 KB |
| pizarra_columna | 80 | ~0,1 KB | ~8 KB |
| pizarra_item | 640 | ~0,15 KB | ~96 KB |
| asistencia | 15.600/año | ~0,2 KB | ~3 MB/año |
| Otros (profesor, grupos, mediciones, etc.) | — | — | ~1–2 MB |
| Índices y overhead MySQL | — | — | ~2–3 MB |

**Total base de datos (año 1):** del orden de **~10–15 MB**.  
En **2–3 años** (más asistencia, más historial): **~25–40 MB** si no se purga nada.

### 2.2 Archivos en disco (uploads)

- **Imágenes de ejercicios:** 100 imágenes. Si son WebP/JPEG optimizados (~50–100 KB c/u) → **5–10 MB**. Si son más pesadas (~200 KB c/u) → **~20 MB**.
- Las imágenes se guardan en filesystem (ej. `uploads/`), no en la base de datos.

### 2.3 Aplicación y sistema

- **JAR de la aplicación:** ~80–100 MB.
- **Logs de la app:** 100–500 MB/año si no se rotan o limpian (conviene rotación por tamaño/tiempo).
- **Sistema operativo y otros:** ya contabilizados en los 5 GB que indicaste.

### 2.4 Resumen almacenamiento

| Concepto | Estimado año 1 | Comentario |
|----------|----------------|------------|
| Base de datos | 10–15 MB | Crece sobre todo por asistencia |
| Imágenes ejercicios | 5–20 MB | Depende del tamaño de cada imagen |
| JAR aplicación | ~90 MB | Fijo |
| Logs | 100–500 MB/año | Reducible con rotación |
| **Total uso app + DB + imágenes** | **~200–650 MB** | Muy por debajo de 20 GB |

Con **4,95 GB usados de 25 GB** actualmente, sumando este uso **no deberías tener inconveniente de espacio** en el corto y mediano plazo. El factor que más crece es **asistencia** (y en menor medida pizarras/rutinas si siguen aumentando); conviene revisar cada tanto el tamaño de la base de datos.

---

## 3. Memoria RAM (2 GB total en el servidor)

- **Spring Boot (Java):** con heap de **~1 GB** la app suele ir fluida para 1–2 usuarios concurrentes (profesor + TV/polling).
- **MySQL:** típicamente **200–400 MB** con una base de este tamaño y pocas conexiones.
- **Sistema operativo y resto:** ~300–500 MB.

**Conclusión:** 2 GB es justo pero **suficiente** para este perfil (pocos usuarios concurrentes, sin colas pesadas). Si más adelante hubiera varios profesores o muchas peticiones simultáneas, el primer paso sería subir RAM (por ejemplo a 4 GB) antes de tocar CPU.

---

## 4. CPU (2 vCPU)

- Uso típico: **1 profesor** editando y **1 cliente** (TV) haciendo polling cada pocos segundos.
- No hay trabajos en segundo plano pesados (solo peticiones web y consultas a la base de datos).

**Conclusión:** 2 vCPU son **más que suficientes** para este escenario. No se prevé inconveniente de CPU con las cifras que diste.

---

## 5. Transferencia (1000 GB/mes)

- Tráfico típico: páginas HTML, CSS, JS, imágenes de ejercicios (muchas en caché), polling de la sala, guardado de pizarra/rutinas.
- Con **1 profesor** y **1 TV** (y eventualmente algún otro dispositivo), el consumo mensual suele estar en el orden de **pocos GB** (por ejemplo 2–10 GB), muy lejos de 1000 GB.

**Conclusión:** No se prevé problema de transferencia con este uso.

---

## 6. Resumen y recomendaciones

| Recurso | Límite / actual | Estimado uso (tu escenario) | ¿Riesgo? |
|---------|-----------------|-----------------------------|----------|
| **Almacenamiento** | 4,95 GB / 25 GB | +0,2–0,65 GB (app + DB + imágenes) | No en corto/medio plazo |
| **RAM** | 2 GB | ~1,5 GB (app + DB + SO) | Ajustado pero suficiente |
| **CPU** | 2 vCPU | Bajo con 1–2 usuarios | No |
| **Transferencia** | 0,66 GB / 1000 GB/mes | Pocos GB/mes | No |

**Recomendaciones para evitar inconvenientes a futuro:**

1. **Logs:** Configurar rotación por tamaño o por tiempo para que no llenen el disco (p. ej. Logback con política por tamaño/antigüedad).
2. **Base de datos:** Cada tanto revisar el tamaño de la BD; el mayor crecimiento vendrá de **asistencia**. Está previsto en el plan de desarrollo implementar un **método de depuración anual de datos** (archivar o purgar asistencia y eventualmente otros datos con más de 1 año). Ver checklist en [ESTADO-PLANES-Y-PENDIENTES.md](ESTADO-PLANES-Y-PENDIENTES.md) y [PLAN_DE_DESARROLLO_UNIFICADO.md](PLAN_DE_DESARROLLO_UNIFICADO.md).
3. **Imágenes:** Mantener ejercicios con imágenes optimizadas (WebP/JPEG comprimido) para no disparar el almacenamiento ni la transferencia.
4. **RAM:** Si en el futuro sumás más usuarios concurrentes o notás lentitud, el primer escalado razonable es **subir a 4 GB RAM** antes de tocar CPU.

En conjunto: con **150 alumnos, 50 series, 100 ejercicios, 50 rutinas y 20 pizarras**, el estimativo indica que **no tendrías inconveniente** en este servidor; el uso queda holgado en disco y transferencia, y la RAM/CPU son suficientes para el perfil de uso descrito. Si más adelante crecen mucho los alumnos, las rutinas o las pizarras, conviene volver a hacer este tipo de estimativo (sobre todo asistencia y tamaño de la BD).

---

*Documento creado Febrero 2026. Cifras orientativas según modelo de datos y escenario indicado por el usuario.*
