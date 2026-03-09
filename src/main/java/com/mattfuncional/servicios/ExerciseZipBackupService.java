package com.mattfuncional.servicios;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mattfuncional.entidades.Exercise;
import com.mattfuncional.entidades.Imagen;
import com.mattfuncional.entidades.Profesor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Exporta e importa ejercicios en ZIP: datos (ejercicios.json) + imágenes en carpeta imagenes/.
 * Al importar, las imágenes se restauran en sus carpetas (uploads) y los ejercicios en BD.
 */
@Service
public class ExerciseZipBackupService {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseZipBackupService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private final ExerciseService exerciseService;
    private final ProfesorService profesorService;
    private final GrupoMuscularService grupoMuscularService;
    private final ImagenServicio imagenServicio;
    private final ObjectMapper objectMapper;

    public ExerciseZipBackupService(ExerciseService exerciseService,
                                    ProfesorService profesorService,
                                    GrupoMuscularService grupoMuscularService,
                                    ImagenServicio imagenServicio) {
        this.exerciseService = exerciseService;
        this.profesorService = profesorService;
        this.grupoMuscularService = grupoMuscularService;
        this.imagenServicio = imagenServicio;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Genera un ZIP con todos los ejercicios del sistema (predeterminados + propios de profesores).
     */
    @Transactional(readOnly = true)
    public byte[] exportarEjerciciosAZip() throws IOException {
        List<Exercise> ejercicios = exerciseService.findAllExercisesWithImages();
        if (ejercicios.isEmpty()) {
            throw new RuntimeException("No hay ejercicios para exportar");
        }
        String origen = "sistema_completo";

        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            // 1. manifest.json
            Map<String, Object> manifest = new HashMap<>();
            manifest.put("version", "1.0");
            manifest.put("fecha", timestamp);
            manifest.put("origen", origen);
            manifest.put("cantidadEjercicios", ejercicios.size());
            byte[] manifestBytes = objectMapper.writeValueAsString(manifest).getBytes(java.nio.charset.StandardCharsets.UTF_8);
            zos.putNextEntry(new ZipEntry("manifest.json"));
            zos.write(manifestBytes);
            zos.closeEntry();

            // 2. ejercicios.json (con referencia a archivo de imagen, sin Base64)
            List<Map<String, Object>> ejerciciosParaJson = new ArrayList<>();
            int index = 0;
            for (Exercise ej : ejercicios) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", ej.getName());
                item.put("description", ej.getDescription());
                item.put("type", ej.getType());
                item.put("videoUrl", ej.getVideoUrl());
                item.put("instructions", ej.getInstructions());
                item.put("benefits", ej.getBenefits());
                item.put("contraindications", ej.getContraindications());
                if (ej.getGrupos() != null && !ej.getGrupos().isEmpty()) {
                    item.put("muscleGroups", ej.getGrupos().stream()
                            .map(com.mattfuncional.entidades.GrupoMuscular::getNombre)
                            .collect(Collectors.toList()));
                }
                if (ej.getImagen() != null) {
                    String ext = extensionDesdeMime(ej.getImagen().getMime());
                    String imagenArchivo = "imagenes/ejercicio_" + index + ext;
                    item.put("imagenArchivo", imagenArchivo);
                    item.put("tieneImagen", true);
                    item.put("mimeType", ej.getImagen().getMime());
                } else {
                    item.put("imagenArchivo", null);
                    item.put("tieneImagen", false);
                }
                ejerciciosParaJson.add(item);
                index++;
            }
            byte[] ejerciciosJsonBytes = objectMapper.writeValueAsString(ejerciciosParaJson).getBytes(java.nio.charset.StandardCharsets.UTF_8);
            zos.putNextEntry(new ZipEntry("ejercicios.json"));
            zos.write(ejerciciosJsonBytes);
            zos.closeEntry();

            // 3. imagenes/
            index = 0;
            for (Exercise ej : ejercicios) {
                if (ej.getImagen() != null) {
                    try {
                        byte[] imagenBytes = imagenServicio.obtenerContenido(ej.getImagen().getId());
                        String ext = extensionDesdeMime(ej.getImagen().getMime());
                        String entryName = "imagenes/ejercicio_" + index + ext;
                        zos.putNextEntry(new ZipEntry(entryName));
                        zos.write(imagenBytes);
                        zos.closeEntry();
                    } catch (Exception e) {
                        logger.warn("Error incluyendo imagen del ejercicio {}: {}", ej.getName(), e.getMessage());
                    }
                }
                index++;
            }
        }

        logger.info("ZIP generado: {} ejercicios, origen={}, {} bytes", ejercicios.size(), origen, baos.size());
        return baos.toByteArray();
    }

    /**
     * Importa ejercicios desde un archivo ZIP subido. Los ejercicios se importan como del sistema (predeterminados).
     * @param pisarTodos si true, se borran todos los ejercicios actuales y se importan los del ZIP; si false, no se graban los que ya existen (por nombre).
     */
    @Transactional
    public Map<String, Object> importarDesdeZip(MultipartFile archivoZip, boolean pisarTodos) throws IOException {
        Map<String, Object> result = new HashMap<>();
        if (archivoZip == null || archivoZip.isEmpty()) {
            result.put("success", false);
            result.put("message", "No se envió ningún archivo");
            return result;
        }
        String nombreOriginal = archivoZip.getOriginalFilename() != null ? archivoZip.getOriginalFilename() : "";
        if (!nombreOriginal.toLowerCase().endsWith(".zip")) {
            result.put("success", false);
            result.put("message", "El archivo debe ser un ZIP exportado desde este sistema");
            return result;
        }

        Map<String, byte[]> zipEntries = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(archivoZip.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                String name = entry.getName();
                if (name.contains("..")) continue;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                int n;
                while ((n = zis.read(buf)) > 0) baos.write(buf, 0, n);
                zipEntries.put(name, baos.toByteArray());
            }
        }

        byte[] ejerciciosJsonBytes = zipEntries.get("ejercicios.json");
        if (ejerciciosJsonBytes == null) {
            result.put("success", false);
            result.put("message", "El ZIP no contiene ejercicios.json");
            return result;
        }

        List<Map<String, Object>> ejerciciosData = objectMapper.readValue(
                new String(ejerciciosJsonBytes, StandardCharsets.UTF_8),
                new TypeReference<List<Map<String, Object>>>() {});

        if (pisarTodos) {
            List<Exercise> existentes = exerciseService.findAllExercisesWithImages();
            for (Exercise e : existentes) {
                exerciseService.deleteExercise(e.getId());
            }
            logger.info("Ejercicios existentes borrados antes de importar: {}", existentes.size());
        }

        int importados = 0;
        int omitidos = 0;
        int conImagen = 0;
        List<String> errores = new ArrayList<>();
        for (Map<String, Object> data : ejerciciosData) {
            try {
                String name = (String) data.get("name");
                if (name == null || name.isBlank()) {
                    errores.add("Ejercicio sin nombre en el ZIP");
                    continue;
                }
                if (!pisarTodos && exerciseService.findByNameAndProfesorNull(name).isPresent()) {
                    omitidos++;
                    continue;
                }
                Exercise ejercicio = new Exercise();
                ejercicio.setName(name);
                ejercicio.setDescription((String) data.get("description"));
                ejercicio.setType((String) data.get("type"));
                ejercicio.setVideoUrl((String) data.get("videoUrl"));
                ejercicio.setInstructions((String) data.get("instructions"));
                ejercicio.setBenefits((String) data.get("benefits"));
                ejercicio.setContraindications((String) data.get("contraindications"));
                ejercicio.setProfesor(null);
                ejercicio.setEsPredeterminado(true);

                if (data.get("muscleGroups") != null) {
                    @SuppressWarnings("unchecked")
                    List<String> nombres = (List<String>) data.get("muscleGroups");
                    ejercicio.setGrupos(grupoMuscularService.resolveGruposByNames(nombres, null));
                }

                String imagenArchivo = (String) data.get("imagenArchivo");
                if (imagenArchivo != null && !imagenArchivo.isBlank()) {
                    byte[] imgBytes = zipEntries.get(imagenArchivo);
                    if (imgBytes != null && imgBytes.length > 0) {
                        String nombreImg = "ejercicio_import_" + System.currentTimeMillis() + "_" + importados;
                        Imagen img = imagenServicio.guardar(imgBytes, nombreImg);
                        ejercicio.setImagen(img);
                        conImagen++;
                    }
                }
                exerciseService.saveExercise(ejercicio, null);
                importados++;
            } catch (Exception e) {
                String nombre = data.get("name") != null ? (String) data.get("name") : "?";
                errores.add(nombre + ": " + e.getMessage());
                logger.warn("Error importando ejercicio {}: {}", nombre, e.getMessage());
            }
        }

        result.put("success", true);
        result.put("ejerciciosImportados", importados);
        result.put("ejerciciosOmitidos", omitidos);
        result.put("ejerciciosConImagen", conImagen);
        if (!errores.isEmpty()) {
            result.put("errores", errores);
        }
        logger.info("Importación ZIP: {} importados, {} omitidos ({} con imagen), pisarTodos={}", importados, omitidos, conImagen, pisarTodos);
        return result;
    }

    private static String extensionDesdeMime(String mime) {
        if (mime == null) return ".jpg";
        if (mime.contains("png")) return ".png";
        if (mime.contains("webp")) return ".webp";
        if (mime.contains("gif")) return ".gif";
        return ".jpg";
    }
}
