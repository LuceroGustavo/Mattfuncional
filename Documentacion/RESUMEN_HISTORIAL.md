# Resumen util de historial (MiGym -> Mattfuncional)

Este archivo resume lo importante de `historial/` para poder borrar esa carpeta y mantener el contexto en `Documentacion/`.

---

## 1. Contexto general del proyecto original
- App original: **MiGym** (Spring Boot + Thymeleaf + MySQL).
- Roles originales: Admin, Profesor, Alumno.
- Modulos principales: ejercicios, series, rutinas, calendario/presentismo, chat, dashboard admin.

## 2. Cambios clave ya implementados

### 2.1 Ejercicios predeterminados (optimizados)
- Imagenes en **un solo lugar**: `uploads/ejercicios/` (no classpath).
- Nombres por numero: `1.webp` .. `60.webp` (o `.gif`).
- El sistema **asegura** los 60 ejercicios si faltan al entrar a "Mis ejercicios".
- Ya no hay boton manual de carga.
- Documento de referencia: `Documentacion/OPTIMIZACION_EJERCICIOS_PREDETERMINADOS.md`.

### 2.2 Imagenes en filesystem
- Migracion completa: se elimina Base64 en BD, se guarda en filesystem.
- `Imagen` ahora guarda `rutaArchivo`, `mime`, `tamanoBytes`.
- Endpoint para servir imagenes: `/img/{id}`.
- Templates actualizados para usar `imagen.url`.

### 2.3 Exportacion / importacion de ejercicios
- Servicio unificado `ExerciseExportImportService`.
- JSON unificado con array directo y `imagenBase64`.
- Importacion corregida sin problemas de transacciones anidadas.
- Backups en `backups/ejercicios/`.

### 2.4 Optimizaciones de desarrollo
- DevTools configurado (hot reload).
- DataInitializer optimizado (solo corre cuando es necesario).
- Perfiles: `application-dev.properties` y `application-railway.properties`.

### 2.5 Detalle del alumno: alumno inactivo y limpieza (Feb 2026)
- Eliminado el botón obsoleto "Asignar Nueva Rutina" al final de la ficha del alumno.
- Cuando el alumno está INACTIVO se desactivan: Progreso, tarjeta "Rutinas asignadas", botón "Asignar rutina", y en la tabla de rutinas los botones Ver, Copiar enlace y WhatsApp. Editar y Eliminar siguen activos.
- Documento: `Documentacion/CHANGELOG_ALUMNO_INACTIVO_Y_BOTON_ASIGNAR_FEB2026.md`.

---

## 3. Decisiones de arquitectura que siguen vigentes
- Ejercicios predeterminados **compartidos** (profesor = null).
- Ejercicios propios por profesor: visibles solo por su creador.
- Series y rutinas basadas en series.
- Alumnos como ficha (sin login), acceso a rutina por enlace (pendiente).

---

## 4. Archivos/areas a recordar
- `scripts/migracion_ejercicios_predeterminados.sql`: migracion de ejercicios predeterminados.
- `uploads/ejercicios/LEEME.txt`: instrucciones de nombres de imagenes 1..60.
- `Documentacion/PLAN_MODIFICACIONES_MATTFUNCIONAL.md`: roadmap principal.
- `Documentacion/PLAN_DE_DESARROLLO.md`: estado de fases.

---

## 5. Pendientes grandes (segun plan)
- **Hecho (Fase 4):** Rutinas por enlace publico (token, Copiar enlace, WhatsApp), asignacion rutina a alumno, hoja de rutina.
- Alumnos sin login (solo ficha) – modelo en curso.
- Pantalla de sala.
- Pagina publica institucional.

