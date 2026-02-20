# Optimización: Ejercicios predeterminados (Mattfuncional)

## Situación anterior (MiGym)
- En MiGym el **admin** cargaba los 60 ejercicios para los profesores.
- Flujo: leer imágenes desde **classpath** (`/static/gif-ejercicios/1.gif`…`60.gif`) → copiar a **uploads/ejercicios/** → crear registros en BD.
- Redundancia: misma imagen en dos sitios (recurso estático + uploads) y proceso pesado (leer, guardar, transacciones).

## Objetivos
1. **Sin redundancia**: Las imágenes solo viven en **uploads/ejercicios/**.
2. **Convención simple**: Tú copias los WebP (o GIF) con nombres `1.webp`, `2.webp`, … `60.webp` en `uploads/ejercicios/`. Si falta un número → ese ejercicio usa imagen por defecto.
3. **Sin botón "Cargar predeterminados"**: El sistema **crea los 60 ejercicios si no existen** (al abrir "Ver ejercicios" o al arrancar), sin borrar nada.
4. **Más rápido y simple**: No leer del classpath ni copiar bytes; solo crear registros en BD y, si existe el archivo, **registrarlo** (no duplicar).

## Diseño nuevo

### 1. Imágenes
- **Origen único**: `uploads/ejercicios/` (ej. `C:\...\Mattfuncional\uploads\ejercicios\`).
- **Nombres**: `1.webp`, `2.webp`, … `60.webp` (o `1.gif`, `2.gif`, … si prefieres GIF).
- El código busca para cada ejercicio N: `N.webp` y, si no existe, `N.gif`. Si no hay archivo → ejercicio con `imagen = null` (la vista muestra `/img/not_imagen.png`).
- **Sin carpeta** `gif-ejercicios` en el proyecto; opcional eliminarla o dejarla vacía.

### 2. Creación de ejercicios
- **Método**: `asegurarEjerciciosPredeterminados()` (o similar).
- **Cuándo**: Al entrar en "Ver ejercicios" del profesor (o en un `ApplicationRunner` al arrancar). Si ya hay 60 predeterminados, no hace nada.
- **Qué hace**:
  - Define la lista fija de 60 ejercicios (nombre, descripción, grupo muscular, etc.).
  - Para cada N = 1..60:
    - Si ya existe un ejercicio predeterminado con ese “número” o nombre estándar, lo omite.
    - Si no existe: crea `Exercise` (predeterminado, sin profesor).
    - Imagen: comprueba si existe `uploads/ejercicios/N.webp` o `N.gif`. Si existe → **registrar** ese archivo en BD (nuevo método `registrarImagenExistente`) y asignar la `Imagen` al ejercicio. Si no → `exercise.setImagen(null)`.

### 3. Registrar imagen existente
- **ImagenServicio**: método `registrarArchivoExistente(String nombreArchivo)`.
  - Comprueba que existe `uploads/ejercicios/{nombreArchivo}`.
  - Crea entidad `Imagen` con `rutaArchivo = nombreArchivo`, mime y nombre según extensión, `tamanoBytes = Files.size(path)`.
  - No copia bytes; el archivo ya está en su sitio.
  - Devuelve la `Imagen` guardada o `null` si el archivo no existe.

### 4. Qué se elimina o deja de usar
- Botón **"Cargar - Predeterminados (60)"** en la vista de ejercicios.
- Lógica que **borra** todos los ejercicios predeterminados y vuelve a cargar.
- Lectura desde **classpath** (`/static/gif-ejercicios/`) para la carga de predeterminados.
- (Opcional) Eliminar o vaciar la carpeta `src/main/resources/static/gif-ejercicios/` para evitar confusiones.

### 5. Nombres de archivos por número (1–60)
Pon en **uploads/ejercicios/** archivos con nombre **solo el número** + extensión `.webp` o `.gif`. El sistema busca primero `N.webp`, si no existe busca `N.gif`. Si no hay ninguno, ese ejercicio usa imagen por defecto.

| Nº | Nombre del ejercicio |
|----|----------------------|
| 1 | Curl de Bíceps con Barra |
| 2 | Extensiones de Tríceps |
| 3 | Curl de Martillo |
| 4 | Fondos |
| 5 | Curl de Bíceps con Mancuernas |
| 6 | Press Francés |
| 7 | Curl de Bíceps en Predicador |
| 8 | Patada de Tríceps |
| 9 | Curl de Bíceps con Cable |
| 10 | Press de Tríceps en Máquina |
| 11 | Sentadillas con Barra |
| 12 | Elevación de Caderas |
| 13 | Peso Muerto |
| 14 | Prensa de Piernas |
| 15 | Zancadas |
| 16 | Extensiones de Cuádriceps |
| 17 | Curl de Isquiotibiales |
| 18 | Elevación de Gemelos |
| 19 | Sentadillas Búlgaras |
| 20 | Hip Thrust |
| 21 | Press de Banca |
| 22 | Press de Banca Inclinado |
| 23 | Aperturas con Mancuernas |
| 24 | Flexiones |
| 25 | Press de Banca Declinado |
| 26 | Dominadas |
| 27 | Remo con Barra |
| 28 | Remo con Cable |
| 29 | Pull Down |
| 30 | Remo Invertido |
| 31 | Press Militar |
| 32 | Elevaciones Laterales |
| 33 | Elevaciones Frontales |
| 34 | Elevaciones Posteriores |
| 35 | Press Arnold |
| 36 | Crunches |
| 37 | Plancha |
| 38 | Russian Twists |
| 39 | Leg Raises |
| 40 | Mountain Climbers Abdomen |
| 41 | Correr |
| 42 | Bicicleta |
| 43 | Saltar Cuerda |
| 44 | Burpees |
| 45 | Jumping Jacks |
| 46 | High Knees |
| 47 | Mountain Climbers Cardio |
| 48 | Squat Jumps |
| 49 | Ciclismo |
| 50 | Subir Escaleras |
| 51 | Estiramiento de Cuádriceps |
| 52 | Estiramiento de Isquiotibiales |
| 53 | Estiramiento de Cadera |
| 54 | Estiramiento de Hombros |
| 55 | Estiramiento de Cuello |
| 56 | Estiramiento de Espalda Baja |
| 57 | Estiramiento de Pecho |
| 58 | Estiramiento de Pantorrillas |
| 59 | Estiramiento de Cuádriceps en Pareja |
| 60 | Estiramiento de Isquiotibiales en Pareja |

### 6. Resumen de ventajas
- Un solo lugar para imágenes: **uploads/ejercicios/**.
- Tú controlas los archivos: copias `1.webp`…`60.webp` (o `.gif`) una vez.
- Sin botón manual: los predeterminados se aseguran solos si no existen.
- Menos código, menos I/O, sin copias redundantes.
- Si falta un archivo, ese ejercicio sigue funcionando con imagen por defecto.
