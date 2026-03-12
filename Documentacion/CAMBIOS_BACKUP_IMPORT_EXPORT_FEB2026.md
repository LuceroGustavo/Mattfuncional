# Cambios: Backup, importación, exportación y correcciones (febrero 2026)

Documentación de todos los cambios realizados desde el sistema de backup (exportar/importar ZIP) hasta los últimos arreglos de importación y panel del profesor.

---

## Sistema de backup — estado terminado (marzo 2026)

El **sistema de backup** se considera **terminado** para el uso actual:

- **Ejercicios + grupos + rutinas + series:** Exportar/importar ZIP (Agregar o Suplantar), con opciones por checkbox (Grupos, Ejercicios, Rutinas, Series). Imágenes con nombres originales.
- **Alumnos — backup:** Exportar/importar JSON (Agregar o Suplantar). Incluye datos del alumno, mediciones físicas y asistencias (observaciones, grupos trabajados).
- **Alumnos — Excel (reportes):** Una hoja con título "Exportación de alumnos fecha dd/MM/yyyy", columnas de datos del alumno (sin peso, sin detalle asistencia, sin contacto emergencia; Fecha de alta en lugar de fecha inicio), Cantidad de asignaciones y una columna final **Último trabajo** (fecha del último progreso + grupos y observaciones, ej. "11/03/26" y debajo "CARDIO - CORE - trabajo muy bien").

Documentación de referencia: `PLAN_BACKUP_Y_EXPORTACION.md`, `EXPORTACION_ALUMNOS_EXCEL.md`.

---

## 6. Backup de alumnos: JSON + Excel separado (marzo 2026)

**Objetivo:** Separar el backup de alumnos (JSON, con importación) de la exportación a Excel (solo reportes).

**Cambios:**
- **Tarjeta superior "Backup de alumnos":** Botón "Exportar backup" (JSON con datos completos: notas del profesor, mediciones, asistencias) y botón "Exportar a Excel" (solo para reportes).
- **Tarjeta inferior "Importar backup de alumnos":** Zona arrastrar/seleccionar archivo JSON; botones Agregar y Suplantar.
- **Servicio:** `AlumnoJsonBackupService` — exporta/importa JSON. Incluye Usuario, MedicionFisica, Asistencia (observaciones, grupos trabajados).
- **Rutas:** GET `/profesor/backup/exportar-alumnos-json`, POST `/profesor/backup/importar-alumnos`, GET `/profesor/backup/exportar-alumnos-excel`.

---

## 1. Redirección tras importar backup

**Objetivo:** Tras importar un ZIP (Agregar o Suplantar), el usuario debe volver a la página de administración en la sección backup, no a una URL separada de backup.

**Cambio:**
- **Archivo:** `AdminPanelController.java`
- **Comportamiento:** Tras `ExerciseZipBackupService.importarDesdeZip(...)`, el controlador redirige a `redirect:/profesor/administracion?seccion=backup` en lugar de `redirect:/profesor/backup`.
- **Efecto:** La barra lateral de administración sigue mostrando "Sistema de backups" y el contenido cargado es el de esa sección; el mensaje de resultado de la importación se muestra en el mismo contexto.

---

## 2. Panel de backup en layout 2×2

**Objetivo:** Organizar la página de backup en dos bloques claros: uno para ejercicios/series/rutinas/grupos y otro para alumnos.

**Cambio:**
- **Archivo:** `src/main/resources/templates/profesor/backup.html`
- **Contenido:**
  - **Columna izquierda:** tarjeta para exportar e importar "Ejercicios + series + rutinas + grupos musculares" (ZIP). Botones "Exportar" e "Importar", zona de arrastre/selección de ZIP, resumen del archivo seleccionado (ejercicios, grupos, rutinas, series, fecha) y botones "Agregar" y "Suplantar".
  - **Columna derecha:** tarjeta en estilo celeste/azul pastel para exportar e importar **alumnos** (Excel y futura importación). Botón "Exportar" (descarga Excel) e "Importar".
- **Estilos:** Diferenciación visual entre la tarjeta de datos de entrenamiento y la de alumnos (colores, iconos).

---

## 3. Exportación de alumnos a Excel

**Objetivo:** Permitir al profesor descargar un Excel con los datos de sus alumnos (sin contraseñas ni historial sensible), con columnas acordadas.

**Cambios:**

### 3.1 Documentación
- **Archivo:** `Documentacion/EXPORTACION_ALUMNOS_EXCEL.md`
- **Contenido:** Definición de columnas del Excel:
  - Datos del alumno: nombre, correo, celular, edad, sexo, peso, estado, **fecha inicio**, **fecha baja**, **tipo de asistencia**, detalle asistencia, **días y horarios** (solo si presencial/semipresencial), objetivos, restricciones, notas, contacto emergencia.
  - **Cantidad de asignaciones** (rutinas asignadas).
  - **Últimas 3 mediciones/evoluciones** (fecha, peso, medidas según modelo).
  - No se exporta: fecha de alta, contraseña, historial de asistencia detallado.

### 3.2 Dependencia
- **Archivo:** `pom.xml`
- **Cambio:** Añadida dependencia `org.apache.poi:poi-ooxml` para generar archivos `.xlsx`.

### 3.3 Servicio de exportación
- **Archivo:** `AlumnoExportService.java` (nuevo)
- **Responsabilidad:** Construir el libro Excel con una hoja de alumnos; por cada alumno (usuario con rol alumno del profesor), rellenar las columnas documentadas, cantidad de asignaciones y últimas 3 mediciones (ordenadas por fecha descendente).
- **Transacción:** `@Transactional(readOnly = true)` para la lectura de usuarios, rutinas asignadas y mediciones.

### 3.4 Repositorio de mediciones
- **Archivo:** `MedicionFisicaRepository.java`
- **Método:** `findByUsuario_IdOrderByFechaDesc` (o equivalente ya existente usado por `MedicionFisicaService`) para obtener las últimas mediciones por alumno.

### 3.5 Endpoint
- **Archivo:** `AdminPanelController.java`
- **Ruta:** `GET /profesor/backup/exportar-alumnos-excel`
- **Comportamiento:** Obtiene el profesor actual, llama a `AlumnoExportService` para generar el Excel y devuelve el archivo como descarga (nombre tipo `alumnos_YYYYMMdd.xlsx`).

### 3.6 Vista
- **Archivo:** `profesor/backup.html`
- **Cambio:** El botón "Exportar" de la tarjeta de alumnos enlaza a `/profesor/backup/exportar-alumnos-excel` (descarga directa).

---

## 4. Error 500 al volver de la importación (Thymeleaf)

**Problema:** Tras importar el ZIP y redirigir a `/profesor/administracion?seccion=backup`, la página devolvía error 500 con mensaje Thymeleaf del tipo: *"Property or field 'errores' cannot be found on object of type 'java.util.HashMap'"*.

**Causa:** El resultado de la importación se pasa al modelo como un `Map` (p. ej. `importResult`). En la plantilla se accedía con sintaxis de propiedad (`importResult.success`, `importResult.errores`). Thymeleaf trata eso como acceso a propiedades del objeto; en un `HashMap` no existen propiedades `success` ni `errores`, sino claves.

**Solución:**
- **Archivo:** `src/main/resources/templates/profesor/administracion.html`
- **Cambio:** Sustituir todos los accesos a `importResult` por sintaxis de mapa:
  - `importResult.success` → `importResult['success']`
  - `importResult.message` → `importResult['message']`
  - `importResult.ejerciciosImportados` → `importResult['ejerciciosImportados']`
  - `importResult.ejerciciosConImagen` → `importResult['ejerciciosConImagen']`
  - `importResult.gruposMuscularesImportados` → `importResult['gruposMuscularesImportados']`
  - `importResult.rutinasImportadas` → `importResult['rutinasImportadas']`
  - `importResult.seriesImportadas` → `importResult['seriesImportadas']`
  - `importResult.ejerciciosOmitidos` → `importResult['ejerciciosOmitidos']`
  - `importResult.errores` → `importResult['errores']`
- **Efecto:** La vista renderiza correctamente el mensaje de éxito o error y la lista de errores (si existe) después de importar.

---

## 5. Conteo de series en el Panel del Profesor tras Suplantar

**Problema:** Al importar un backup con "Suplantar", el ZIP contenía 8 series y el log indicaba "Restore completo: 2 rutinas, 8 series (pisarTodos=true)". En el Panel del Profesor la tarjeta "Series" mostraba **4** en lugar de 8.

**Causa:** El dashboard del profesor no cuenta todas las series, sino solo las **series plantilla** del profesor: `serieService.obtenerSeriesPlantillaPorProfesor(profesor.getId())`, que en repositorio equivale a `findByProfesorIdAndEsPlantillaTrue(profesorId)`. En el backup, parte de las series podían haberse exportado con `esPlantilla = false` (p. ej. copias o series no plantilla). Al restaurar, se respetaba el valor del JSON y esas series quedaban con `esPlantilla = false`, por lo que no se incluían en el conteo del panel.

**Solución:**
- **Archivo:** `ExerciseZipBackupService.java`
- **Cambio:** En el bloque de restauración de series (backup completo con `rutinas.json` y `series.json`), al crear cada `Serie` restaurada se fuerza `serie.setEsPlantilla(true)` en lugar de leer `sd.get("esPlantilla")` del JSON.
- **Comentario en código:** "En restore completo todas las series se consideran plantilla para que el panel muestre el total correcto."
- **Efecto:** Las 8 series (y en general todas las series del backup) se restauran como plantilla; el Panel del Profesor muestra el número correcto (8) en la tarjeta "Series".

**Nota:** El borrado previo de series y rutinas en modo Suplantar ya era correcto (`serieRepository.deleteAll()` y `rutinaRepository.deleteAll()` cuando el ZIP es backup completo); no se modificó esa lógica.

---

## Resumen de archivos tocados

| Área | Archivos |
|------|----------|
| Redirección post-import | `AdminPanelController.java` |
| Panel backup 2×2 + enlace exportar alumnos | `profesor/backup.html` |
| Exportación Excel alumnos | `pom.xml`, `AlumnoExportService.java`, `MedicionFisicaRepository.java` (o uso en servicio), `AdminPanelController.java` |
| Error 500 Thymeleaf | `profesor/administracion.html` |
| Conteo series en dashboard | `ExerciseZipBackupService.java` |
| Documentación | `Documentacion/EXPORTACION_ALUMNOS_EXCEL.md`, `Documentacion/CAMBIOS_BACKUP_IMPORT_EXPORT_FEB2026.md` (este archivo) |

---

*Documento generado para registro de cambios de backup, importación, exportación de alumnos y correcciones asociadas (febrero 2026).*
