# Plan de desarrollo: Backup y exportación (ejercicios, alumnos, rutinas, series)

**Objetivo:** Permitir crear backups/exportaciones que se puedan usar en otra instalación o en otra app, y exportar datos a Excel para análisis o respaldo.

**Estado:** En curso — Fase 1 (backup ejercicios ZIP) **implementada**. Restauración completa de series (standalone y por rutina) y rutinas aplicada. Pendiente: fases 2–5; mejorar estilo y excepciones en el servicio de backup.

---

## Implementación actual: series y rutinas (export/import)

### Modelo de datos respetado

- **Serie** = grupo de ejercicios (lista de `SerieEjercicio`). Una serie puede tener el mismo ejercicio varias veces con distinto peso y repeticiones.
- **Rutina** = grupo de series. Una rutina contiene N series.
- **Usuario (alumno)** puede tener varias rutinas asignadas (o ninguna).

No se asume que todo ejercicio esté en una serie, que toda serie esté en una rutina ni que todo alumno tenga rutinas.

### Comportamiento implementado

| Aspecto | Export | Import |
|--------|--------|--------|
| **Series sin rutina** | Se exportan todas las series plantilla con `rutina_id` null (`findByEsPlantillaTrueAndRutinaIsNull()`), primero en `series.json`, con `rutinaIndex: null`. | Si `rutinaIndex` es null o ausente, la serie se crea con `rutina = null` (aparece en "Mis Series"). |
| **Series en rutinas** | Por cada rutina plantilla se exportan sus series con `rutinaIndex` = índice de esa rutina en `rutinas.json`. | Se asigna la rutina correspondiente de `rutinasCreadas` según `rutinaIndex`. |
| **SerieEjercicio** | Se exporta la lista completa por serie (exerciseName, valor, unidad, peso, orden). El mismo ejercicio puede repetirse con distintos datos. | Se recrean todos los ítems; se respeta "mismo ejercicio varias veces con distinto peso/repetición". |
| **Modo Agregar** | — | Si una rutina ya existe (mismo nombre y profesor), se usa esa rutina para vincular las series de ese índice en lugar de omitirlas. |

### Archivos involucrados

- `ExerciseZipBackupService.java`: export con series standalone + series por rutina; método `serieToMap(Serie, Integer rutinaIndex)`; import con `rutinaIndex` opcional (null = serie sin rutina).
- `SerieRepository.java`: `findByEsPlantillaTrueAndRutinaIsNull()` para listar series sin rutina.
- `profesor/backup.html`: uso de `importResult` como mapa (notación `importResult['errores']`, etc.) para compatibilidad con Thymeleaf.

### Pendiente mejorar

- **Estilo:** Revisar nombres de variables, extraer constantes, comentarios y formato del código en `ExerciseZipBackupService` para mantener consistencia con el resto del proyecto.
- **Excepciones:** Unificar manejo de errores (validación del ZIP, JSON malformado, rutina/serie duplicada en modo Agregar, fallos al guardar imagen o entidad). Considerar excepciones de dominio (p. ej. `BackupImportException`) y mensajes claros al usuario en lugar de fallos genéricos.

---

## Cambios y mejoras recientes (2026-03-09)

### Implementado

- **Nombres de imágenes originales en export/import**
  - **Exportar:** Usa el nombre original de la imagen en BD (`rutaArchivo`, ej. `1.webp`, `2.webp`) en lugar de `ejercicio_0.jpg`, `ejercicio_1.gif`. Solo si no hay nombre se usa fallback `ejercicio_N.ext`.
  - **Importar:** `ImagenServicio.guardarParaRestore(byte[], rutaEnZip)` extrae el nombre del archivo del ZIP (ej. `imagenes/1.webp` → `1.webp`) y guarda con ese nombre. Preserva formato (gif/webp sin optimizar para no perder animación).
- **Restauración de rutinas y series**
  - Backup completo incluye `rutinas.json` y `series.json`. Al importar con "Suplantar" o "Agregar", se restauran rutinas y series.
  - Las rutinas y series se asignan al **profesor logueado** (no al primero de la BD). `importarDesdeZip` recibe `Profesor profesorParaRestore` desde `AdminPanelController`.
- **Eliminación de ejercicios:** `ExerciseService.deleteExercise()` ahora elimina también el archivo físico de la imagen (`ImagenServicio.eliminarImagen()`).

### Archivos modificados

| Archivo | Cambios |
|---------|---------|
| `ExerciseZipBackupService.java` | Export con `rutaArchivo` original; import con `guardarParaRestore(bytes, rutaEnZip)`; parámetro `Profesor` para rutinas/series |
| `ImagenServicio.java` | `guardarParaRestore(byte[], String rutaEnZip)` — usa nombre del ZIP |
| `AdminPanelController.java` | Inyecta `ProfesorService`; pasa profesor del usuario a `importarDesdeZip` |
| `ExerciseService.java` | `deleteExercise()` elimina archivo físico de imagen |
| `RutinaRepository.java` | `findByNombreAndEsPlantillaTrueAndProfesorId()` para evitar duplicados en modo Agregar |

### Pendiente testear

- [ ] Exportar backup con ejercicios que tengan imágenes `1.webp`, `2.webp`, etc. → verificar que el ZIP contiene `imagenes/1.webp`, `imagenes/2.webp`.
- [ ] Importar ese ZIP con "Suplantar" → verificar que las imágenes se guardan como `1.webp`, `2.webp` en `uploads/ejercicios/` y que se ven en la lista de ejercicios.
- [ ] Importar backup completo (ejercicios + rutinas + series) con "Suplantar" → verificar que las series aparecen en el panel del profesor (contador "Series" > 0).
- [ ] Importar con "Agregar" → verificar que no duplica rutinas existentes por nombre.
- [ ] Importar backup antiguo con `ejercicio_0.jpg` en el ZIP → verificar que se restaura correctamente (nombre `ejercicio_0.jpg`).

---

## Hasta donde llegamos (para retomar mañana)

### Implementado

- **Backup de ejercicios en ZIP (Fase 1)**
  - **Exportar:** Tarjeta con botón. Genera ZIP y descarga al navegador (carpeta predefinida). Contenido: `manifest.json`, `ejercicios.json`, carpeta `imagenes/` (con nombres originales: 1.webp, 2.webp, etc.). Modal de progreso durante la exportación.
  - **Importar:** Zona arrastrar/seleccionar. Al seleccionar ZIP se lee `manifest.json` (JSZip client-side) y se muestra detalle: cantidad ejercicios, rutinas, series, fecha. Dos botones: **Agregar ejercicios** (suma, omite existentes) y **Suplantar ejercicios** (borra todos e importa; aviso de impacto en series/rutinas; confirmación). POST `/profesor/backup/importar` (form) y `/profesor/backup/importar-ejercicios` (JSON). Modal de progreso.
  - **Servicio:** `ExerciseZipBackupService` — `exportarEjerciciosAZip()`, `importarDesdeZip(MultipartFile, boolean pisarTodos, Profesor profesorParaRestore)`.
  - **Controlador:** `AdminPanelController`: GET `/profesor/backup`, GET `/profesor/backup/exportar-zip`, POST `/profesor/backup/importar`, POST `/profesor/backup/importar-ejercicios` (retorna JSON).
  - **Vista:** `profesor/backup.html` — Tarjetas: (1) Exportar ejercicios, (2) Importar ejercicios (detalle archivo + botones Agregar/Suplantar), (3–9) placeholders series/rutinas/alumnos. Sin carpeta backup servidor. Modal progreso durante export/import.
  - **Enlaces:** El botón «Backup y resguardo» en `/profesor/usuarios-sistema` y en `/profesor/pagina-publica` lleva a `/profesor/backup`.

### Pendiente / para mejorar mañana

- Fase 2: exportar alumnos a Excel.
- Fases 3–5: rutinas/series a JSON (y opcional Excel), importación opcional.

---

## 1. Situación actual

| Funcionalidad | Estado | Notas |
|---------------|--------|--------|
| **Ejercicios – JSON** | Existe | `ExerciseBackupService`: exporta/restaura solo JSON (sin imágenes). `ExerciseExportImportService`: exporta JSON con imágenes en Base64; importa desde ese JSON. |
| **Ejercicios – ZIP** | **Implementado** | `ExerciseZipBackupService`: exporta todos los ejercicios a ZIP (manifest + ejercicios.json + imagenes/); importar desde archivo subido con opción «no duplicar» o «pisar todos». |
| **Alumnos – Excel** | No existe | No hay exportación de alumnos a Excel. |
| **Rutinas / Series – export** | No existe | No hay exportación de rutinas ni series. |

---

## 2. Backup de ejercicios (ZIP con datos + imágenes)

### 2.1 Objetivo

- Un **único archivo ZIP** descargable que contenga:
  - Datos de ejercicios en un archivo (JSON o similar).
  - Imágenes en una carpeta (p. ej. `imagenes/`) con nombres identificables.
- Poder **importar** ese ZIP desde esta app o desde otra (misma estructura).

### 2.2 Estructura propuesta del ZIP

```
ejercicios_backup_YYYY-MM-DD_HH-mm.zip
├── manifest.json          # Metadatos: versión, fecha, origen (profesor/sistema), cantidad
├── ejercicios.json        # Array de ejercicios (sin Base64; referencias a archivo de imagen)
└── imagenes/
    ├── ejercicio_1.jpg    # id o nombre normalizado
    ├── ejercicio_2.png
    └── ...
```

- **manifest.json:** `{ "version": "1.0", "fecha": "...", "origen": "sistema|profesor_ID", "cantidadEjercicios": N }`.
- **ejercicios.json:** Cada ítem con campos: name, description, type, videoUrl, instructions, benefits, contraindications, muscleGroups (nombres), y `"imagenArchivo": "imagenes/ejercicio_1.jpg"` (o null si no tiene).
- Las imágenes se guardan como archivos binarios en `imagenes/` (evita JSON gigante con Base64).

### 2.3 Flujo técnico

1. **Exportar a ZIP**
   - Servicio (p. ej. ampliar `ExerciseExportImportService` o nuevo `ExerciseZipBackupService`):
     - Obtener lista de ejercicios (sistema o por profesor).
     - Crear `ejercicios.json` con datos y referencia al archivo de imagen por ejercicio.
     - Por cada ejercicio con imagen: copiar bytes a un nombre único en `imagenes/` dentro del ZIP.
     - Crear `manifest.json`.
     - Empaquetar todo en un ZIP (usar `java.util.zip.ZipOutputStream` o similar).
   - Controlador: devolver el ZIP como `ResponseEntity<Resource>` con `Content-Disposition: attachment; filename="..."`.

2. **Importar desde ZIP**
   - Endpoint recibe el archivo ZIP (MultipartFile).
   - Descomprimir en memoria o en temp:
     - Leer `manifest.json` y `ejercicios.json`.
     - Por cada ejercicio: si tiene `imagenArchivo`, leer el archivo desde la carpeta dentro del ZIP y guardar imagen (ImagenServicio); luego crear Exercise y asociar imagen. Si no, crear Exercise sin imagen.
   - Resolver grupos musculares por nombre (como en importación JSON actual).
   - Destino: profesor elegido (o “sistema” si se define ese flujo).

### 2.4 Dónde implementar

- **Servicio:** Nuevo `ExerciseZipBackupService` o métodos en `ExerciseExportImportService`: `exportarEjerciciosAZip(Long profesorId)` → byte[] o Path; `importarEjerciciosDesdeZip(MultipartFile zip, Long profesorDestinoId, boolean limpiarAnteriores)`.
- **Controlador:** Endpoints en `ExerciseController` o en el controlador de admin/profesor que ya use backup (GET descarga ZIP, POST sube ZIP para importar).
- **UI:** Botones “Descargar backup ZIP” e “Importar desde ZIP” en la pantalla de backup de ejercicios. **Importar** no usa la carpeta backup del servidor: el usuario arrastra el archivo ZIP o lo selecciona (zona «Arrastrá el backup acá o seleccioná archivo para importar»).

---

## 3. Exportación de alumnos a Excel

### 3.1 Objetivo

- Exportar **solo datos de alumnos** (atributos) a un Excel para respaldo, análisis o uso externo.
- No exportar contraseñas ni datos sensibles innecesarios.

### 3.2 Qué exportar (atributos de Usuario/alumno)

Incluir columnas como (según entidad `Usuario`):

- id, nombre, correo, edad, sexo, peso  
- celular, estadoAlumno, fechaAlta, fechaBaja, fechaInicio  
- notasProfesor, objetivosPersonales, restriccionesMedicas  
- contactoEmergenciaNombre, contactoEmergenciaTelefono  
- detalleAsistencia, tipoAsistencia (si aplica)  
- profesor (nombre o id del profesor asignado)

No incluir: password, avatar (o incluir solo ruta/URL si se desea), colecciones complejas como rutinas completas (opcional: una columna “cantidad de rutinas” si sirve).

### 3.3 Método recomendado

- **Formato:** Excel (.xlsx).
- **Librería:** Apache POI (p. ej. `org.apache.poi:poi-ooxml`). Añadir dependencia en `pom.xml`.
- **Servicio:** `AlumnoExportService` o `UsuarioExportService` con método `byte[] exportarAlumnosAExcel(Long profesorId)` (o todos los alumnos del sistema si es admin):
  - Filtrar usuarios con rol ALUMNO (y opcionalmente por profesor).
  - Crear workbook, hoja “Alumnos”, cabecera con nombres de columnas, filas con los atributos.
  - Escribir a `ByteArrayOutputStream` y devolver `byte[]`.
- **Controlador:** GET (o POST) que devuelva el Excel con `Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` y `Content-Disposition: attachment; filename="alumnos_YYYY-MM-DD.xlsx"`.

### 3.4 Importación de alumnos desde Excel (opcional, fase posterior)

- No es obligatorio para el primer entregable. Si más adelante se desea importar alumnos desde Excel, conviene definir columnas fijas y validaciones (correo único, etc.) y un endpoint POST que reciba el archivo y procese fila a fila.

---

## 4. Exportación de rutinas y series

### 4.1 Opciones

**A) Solo datos tabulares en Excel**  
- Una hoja “Rutinas” (id, nombre, descripción, estado, esPlantilla, categoria, profesor, usuario asignado, fechas, etc.).  
- Una hoja “Series” (id, nombre, repeticionesSerie, rutina_id, profesor_id).  
- Una hoja “Series – Ejercicios” (serie_id, ejercicio_id, orden, valor, unidad, peso).  
- Ventaja: fácil de leer y abrir en cualquier herramienta.  
- Limitación: no incluye “contenido” de ejercicios (nombres de ejercicios habría que resolver por id o añadir columna nombre).

**B) JSON (recomendado para backup/portabilidad)**  
- Un JSON (o ZIP con JSON) que describa rutinas plantilla con sus series y, para cada serie, la lista de ejercicios con nombre o id, valor, unidad, peso.  
- Ventaja: se puede re-importar en la misma app (o otra que entienda el formato) y reconstruir rutinas/series; se puede incluir solo IDs de ejercicio o también “snapshot” del nombre del ejercicio para legibilidad.  
- Estructura sugerida:  
  - `rutinas.json`: array de `{ id, nombre, descripcion, estado, esPlantilla, categoria, series: [ { id, nombre, repeticionesSerie, ejercicios: [ { ejercicioId o ejercicioNombre, orden, valor, unidad, peso } ] } ] }`.  
- Si se exporta para “otra app”, conviene incluir nombres de ejercicios además de ids, por si en destino los ids son distintos.

**C) Híbrido**  
- Exportar a Excel para reportes/lectura humana.  
- Exportar/importar a JSON (o ZIP con JSON) para backup y portabilidad.

### 4.2 Recomendación

- **Rutinas y series:** Priorizar **exportación a JSON** (y opcionalmente ZIP si se quiere un solo archivo) para backup e intercambio entre instalaciones. Dejar **Excel** para una segunda fase si hace falta para reportes.
- **Alumnos:** Como comentaste, **solo exportar a Excel los datos del alumno con sus atributos** es suficiente y coherente; no hace falta JSON para alumnos en un primer paso.

---

## 5. Orden de implementación sugerido

| Fase | Tarea | Entregable |
|------|--------|------------|
| **1** | Backup ejercicios en ZIP (datos + imágenes) | Servicio + endpoint descarga ZIP; opcional: importar desde ZIP. |
| **2** | Exportar alumnos a Excel | Dependencia POI, servicio, endpoint descarga .xlsx. |
| **3** | Exportar rutinas/series a JSON | Servicio que serialice rutinas plantilla (y sus series y ejercicios) a JSON; endpoint descarga. |
| **4** | (Opcional) Importar rutinas/series desde JSON | Endpoint que reciba JSON y cree rutinas/series (resolviendo ejercicios por id o nombre). |
| **5** | (Opcional) Exportar rutinas/series a Excel | Hoja(s) Excel para reporte. |

---

## 6. Dependencias y ubicación de código

- **ZIP:** `java.util.zip` (JDK). No hace falta dependencia extra.
- **Excel:** Añadir en `pom.xml`:
  ```xml
  <dependency>
      <groupId>org.apache.poi</groupId>
      <artifactId>poi-ooxml</artifactId>
      <version>5.2.5</version>
  </dependency>
  ```
- **Servicios:**  
  - `ExerciseZipBackupService` (o dentro de `ExerciseExportImportService`).  
  - `AlumnoExportService` o `UsuarioExportService` (export Excel).  
  - `RutinaSerieExportService` (export JSON; luego import si se hace).
- **Controladores:** Reutilizar o ampliar los que ya exponen backup (ej. panel admin o profesor) para descargar ZIP y Excel; rutinas/series en el controlador de rutinas o panel.

---

## 7. Resumen de criterios de diseño

- **Ejercicios:** Un ZIP con datos (JSON) + imágenes en archivos separados para poder importar en otra app u otra instalación.
- **Alumnos:** Solo datos en Excel (atributos del alumno), sin contraseñas; formato .xlsx con una hoja “Alumnos”.
- **Rutinas y series:** Exportación a JSON (recomendada primero) para backup e intercambio; Excel como opción posterior para reportes.
- **Seguridad:** Endpoints de export/import restringidos por rol (admin/profesor); validar profesorId y que los datos pertenezcan al profesor cuando aplique.

Si no se termina en un solo bloque, este plan permite retomar desde otro PC: seguir por fases (ZIP ejercicios → Excel alumnos → JSON rutinas/series) y reutilizar la estructura aquí descrita.
