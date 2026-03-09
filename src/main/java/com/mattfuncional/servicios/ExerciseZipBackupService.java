package com.mattfuncional.servicios;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mattfuncional.entidades.Exercise;
import com.mattfuncional.entidades.Imagen;
import com.mattfuncional.entidades.Profesor;
import com.mattfuncional.entidades.Rutina;
import com.mattfuncional.entidades.Serie;
import com.mattfuncional.entidades.SerieEjercicio;
import com.mattfuncional.repositorios.PizarraItemRepository;
import com.mattfuncional.repositorios.ProfesorRepository;
import com.mattfuncional.repositorios.RutinaRepository;
import com.mattfuncional.repositorios.SerieEjercicioRepository;
import com.mattfuncional.repositorios.SerieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Exporta e importa ejercicios, series y rutinas en ZIP.
 * Backup completo: ejercicios.json + rutinas.json + series.json + imagenes/.
 * Al restaurar con "suplantar", todo queda tal cual estaba.
 */
@Service
public class ExerciseZipBackupService {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseZipBackupService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private final ExerciseService exerciseService;
    private final GrupoMuscularService grupoMuscularService;
    private final ImagenServicio imagenServicio;
    private final SerieEjercicioRepository serieEjercicioRepository;
    private final PizarraItemRepository pizarraItemRepository;
    private final RutinaRepository rutinaRepository;
    private final SerieRepository serieRepository;
    private final ProfesorRepository profesorRepository;
    private final PlatformTransactionManager transactionManager;
    private final ObjectMapper objectMapper;

    public ExerciseZipBackupService(ExerciseService exerciseService,
                                    GrupoMuscularService grupoMuscularService,
                                    ImagenServicio imagenServicio,
                                    SerieEjercicioRepository serieEjercicioRepository,
                                    PizarraItemRepository pizarraItemRepository,
                                    RutinaRepository rutinaRepository,
                                    SerieRepository serieRepository,
                                    ProfesorRepository profesorRepository,
                                    PlatformTransactionManager transactionManager) {
        this.exerciseService = exerciseService;
        this.grupoMuscularService = grupoMuscularService;
        this.imagenServicio = imagenServicio;
        this.serieEjercicioRepository = serieEjercicioRepository;
        this.pizarraItemRepository = pizarraItemRepository;
        this.rutinaRepository = rutinaRepository;
        this.serieRepository = serieRepository;
        this.profesorRepository = profesorRepository;
        this.transactionManager = transactionManager;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Genera un ZIP con ejercicios, rutinas plantilla y sus series. Restaurar devuelve todo tal cual.
     */
    @Transactional(readOnly = true)
    public byte[] exportarEjerciciosAZip() throws IOException {
        List<Exercise> ejercicios = exerciseService.findAllExercisesWithImages();
        if (ejercicios.isEmpty()) {
            throw new RuntimeException("No hay ejercicios para exportar");
        }
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Rutina> rutinasPlantilla = rutinaRepository.findByEsPlantillaTrue();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            // 1. manifest.json
            int totalSeries = 0;
            for (Rutina r : rutinasPlantilla) {
                Rutina rConSeries = rutinaRepository.findByIdWithSeries(r.getId()).orElse(r);
                if (rConSeries.getSeries() != null) totalSeries += rConSeries.getSeries().size();
            }
            Map<String, Object> manifest = new HashMap<>();
            manifest.put("version", "1.0");
            manifest.put("tipo", "completo");
            manifest.put("fecha", timestamp);
            manifest.put("cantidadEjercicios", ejercicios.size());
            manifest.put("cantidadRutinas", rutinasPlantilla.size());
            manifest.put("cantidadSeries", totalSeries);
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
                    String nombreOriginal = ej.getImagen().getRutaArchivo();
                    String imagenArchivo = (nombreOriginal != null && !nombreOriginal.isBlank())
                        ? "imagenes/" + nombreOriginal
                        : "imagenes/ejercicio_" + index + extensionDesdeMime(ej.getImagen().getMime());
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

            // 3. imagenes/ (usa nombre original: 1.webp, 2.webp, etc.)
            index = 0;
            for (Exercise ej : ejercicios) {
                if (ej.getImagen() != null) {
                    try {
                        byte[] imagenBytes = imagenServicio.obtenerContenido(ej.getImagen().getId());
                        String nombreOriginal = ej.getImagen().getRutaArchivo();
                        String entryName = (nombreOriginal != null && !nombreOriginal.isBlank())
                            ? "imagenes/" + nombreOriginal
                            : "imagenes/ejercicio_" + index + extensionDesdeMime(ej.getImagen().getMime());
                        zos.putNextEntry(new ZipEntry(entryName));
                        zos.write(imagenBytes);
                        zos.closeEntry();
                    } catch (Exception e) {
                        logger.warn("Error incluyendo imagen del ejercicio {}: {}", ej.getName(), e.getMessage());
                    }
                }
                index++;
            }
            // 4. rutinas.json y series.json (backup completo)
            List<Map<String, Object>> rutinasParaJson = new ArrayList<>();
            List<Map<String, Object>> seriesParaJson = new ArrayList<>();
            int rutinaIndex = 0;
            for (Rutina rutina : rutinasPlantilla) {
                Rutina rConSeries = rutinaRepository.findByIdWithSeries(rutina.getId()).orElse(rutina);
                Map<String, Object> rutinaItem = new HashMap<>();
                rutinaItem.put("nombre", rutina.getNombre());
                rutinaItem.put("descripcion", rutina.getDescripcion());
                rutinaItem.put("estado", rutina.getEstado());
                rutinaItem.put("categoria", rutina.getCategoria());
                rutinaItem.put("creador", rutina.getCreador());
                rutinaItem.put("esPlantilla", rutina.isEsPlantilla());
                rutinasParaJson.add(rutinaItem);

                if (rConSeries.getSeries() != null) {
                    List<Serie> seriesOrdenadas = new ArrayList<>(rConSeries.getSeries());
                    seriesOrdenadas.sort((a, b) -> Integer.compare(a.getOrden(), b.getOrden()));
                    for (Serie serie : seriesOrdenadas) {
                        Serie sConEj = serieRepository.findByIdWithSerieEjercicios(serie.getId()).orElse(serie);
                        Map<String, Object> serieItem = new HashMap<>();
                        serieItem.put("rutinaIndex", rutinaIndex);
                        serieItem.put("orden", serie.getOrden());
                        serieItem.put("nombre", serie.getNombre());
                        serieItem.put("descripcion", serie.getDescripcion());
                        serieItem.put("esPlantilla", serie.isEsPlantilla());
                        serieItem.put("repeticionesSerie", serie.getRepeticionesSerie());
                        serieItem.put("creador", serie.getCreador());
                        List<Map<String, Object>> seList = new ArrayList<>();
                        if (sConEj.getSerieEjercicios() != null) {
                            List<SerieEjercicio> seOrdenados = new ArrayList<>(sConEj.getSerieEjercicios());
                            seOrdenados.sort((a, b) -> Integer.compare(
                                    a.getOrden() != null ? a.getOrden().intValue() : 0,
                                    b.getOrden() != null ? b.getOrden().intValue() : 0));
                            for (SerieEjercicio se : seOrdenados) {
                                if (se.getExercise() != null) {
                                    Map<String, Object> seItem = new LinkedHashMap<>();
                                    seItem.put("exerciseName", se.getExercise().getName());
                                    seItem.put("valor", se.getValor());
                                    seItem.put("unidad", se.getUnidad());
                                    seItem.put("peso", se.getPeso());
                                    seItem.put("orden", se.getOrden() != null ? se.getOrden().intValue() : 0);
                                    seList.add(seItem);
                                }
                            }
                        }
                        serieItem.put("serieEjercicios", seList);
                        seriesParaJson.add(serieItem);
                    }
                }
                rutinaIndex++;
            }
            byte[] rutinasBytes = objectMapper.writeValueAsString(rutinasParaJson).getBytes(StandardCharsets.UTF_8);
            zos.putNextEntry(new ZipEntry("rutinas.json"));
            zos.write(rutinasBytes);
            zos.closeEntry();
            byte[] seriesBytes = objectMapper.writeValueAsString(seriesParaJson).getBytes(StandardCharsets.UTF_8);
            zos.putNextEntry(new ZipEntry("series.json"));
            zos.write(seriesBytes);
            zos.closeEntry();
        }

        logger.info("ZIP generado: {} ejercicios, {} rutinas, {} bytes", ejercicios.size(), rutinasPlantilla.size(), baos.size());
        return baos.toByteArray();
    }

    /**
     * Importa ejercicios desde un archivo ZIP subido. Los ejercicios se importan como del sistema (predeterminados).
     * @param pisarTodos si true, se borran todos los ejercicios actuales y se importan los del ZIP; si false, no se graban los que ya existen (por nombre).
     * @param profesorParaRestore profesor al que se asignan rutinas y series; si null, se usa el primero del sistema.
     */
    @Transactional
    public Map<String, Object> importarDesdeZip(MultipartFile archivoZip, boolean pisarTodos, Profesor profesorParaRestore) throws IOException {
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

        boolean esBackupCompleto = zipEntries.containsKey("rutinas.json") && zipEntries.containsKey("series.json");

        if (pisarTodos) {
            // Orden: SerieEjercicio, PizarraItem, Serie, Rutina, Exercise (respeta FK)
            int serieEjerciciosEliminados = serieEjercicioRepository.deleteAllWithExercise();
            int pizarraItemsEliminados = pizarraItemRepository.deleteAllItems();
            logger.info("Referencias eliminadas: {} SerieEjercicio, {} PizarraItem", serieEjerciciosEliminados, pizarraItemsEliminados);

            if (esBackupCompleto) {
                serieRepository.deleteAll();
                rutinaRepository.deleteAll();
                logger.info("Series y rutinas eliminadas para restore completo");
            }

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
        Map<String, byte[]> zipEntriesFinal = zipEntries; // para uso en lambda
        int ejercicioIndex = 0;
        for (Map<String, Object> data : ejerciciosData) {
            String name = (String) data.get("name");
            if (name == null || name.isBlank()) {
                errores.add("Ejercicio sin nombre en el ZIP");
                continue;
            }
            if (!pisarTodos && exerciseService.findByNameAndProfesorNull(name).isPresent()) {
                omitidos++;
                continue;
            }
            // Cada ejercicio en su propia transacción (REQUIRES_NEW): si uno falla, no afecta al resto
            DefaultTransactionDefinition def = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionTemplate newTxTemplate = new TransactionTemplate(transactionManager, def);
            final String nombreEjercicio = name;
            Boolean ok = newTxTemplate.execute(status -> {
                try {
                    Exercise ejercicio = new Exercise();
                    ejercicio.setName(nombreEjercicio);
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
                        byte[] imgBytes = zipEntriesFinal.get(imagenArchivo);
                        if (imgBytes != null && imgBytes.length > 0) {
                            Imagen img = imagenServicio.guardarParaRestore(imgBytes, imagenArchivo);
                            ejercicio.setImagen(img);
                        }
                    }
                    exerciseService.saveExercise(ejercicio, null);
                    return true;
                } catch (Exception e) {
                    errores.add(nombreEjercicio + ": " + e.getMessage());
                    logger.warn("Error importando ejercicio {}: {}", nombreEjercicio, e.getMessage());
                    status.setRollbackOnly(); // solo esta transacción interna
                    return false;
                }
            });
            if (Boolean.TRUE.equals(ok)) {
                importados++;
                if (data.get("imagenArchivo") != null && !((String) data.get("imagenArchivo")).isBlank()) {
                    byte[] imgBytes = zipEntriesFinal.get((String) data.get("imagenArchivo"));
                    if (imgBytes != null && imgBytes.length > 0) conImagen++;
                }
            }
        }

        // Si es backup completo (imágenes + series + rutinas), restaurar rutinas y series
        int rutinasImportadas = 0;
        int seriesImportadas = 0;
        if (esBackupCompleto) {
            Map<String, Exercise> ejercicioPorNombre = new HashMap<>();
            for (Exercise ex : exerciseService.findAllExercisesWithImages()) {
                ejercicioPorNombre.put(ex.getName(), ex);
            }
            Profesor profesorRestore = profesorParaRestore != null
                    ? profesorParaRestore
                    : profesorRepository.findAll().stream().findFirst()
                            .orElseThrow(() -> new RuntimeException("No hay profesor en el sistema para restaurar rutinas"));

            byte[] rutinasBytes = zipEntries.get("rutinas.json");
            byte[] seriesBytes = zipEntries.get("series.json");
            if (rutinasBytes != null && seriesBytes != null) {
                List<Map<String, Object>> rutinasData = objectMapper.readValue(
                        new String(rutinasBytes, StandardCharsets.UTF_8),
                        new TypeReference<List<Map<String, Object>>>() {});
                List<Map<String, Object>> seriesData = objectMapper.readValue(
                        new String(seriesBytes, StandardCharsets.UTF_8),
                        new TypeReference<List<Map<String, Object>>>() {});

                List<Rutina> rutinasCreadas = new ArrayList<>();
                for (Map<String, Object> rd : rutinasData) {
                    String nombreRutina = (String) rd.get("nombre");
                    if (nombreRutina == null || nombreRutina.isBlank()) continue;
                    if (!pisarTodos && rutinaRepository.findByNombreAndEsPlantillaTrueAndProfesorId(nombreRutina, profesorRestore.getId()).isPresent()) {
                        rutinasCreadas.add(null);
                        continue;
                    }
                    Rutina rutina = new Rutina();
                    rutina.setNombre(nombreRutina);
                    rutina.setDescripcion((String) rd.get("descripcion"));
                    rutina.setEstado(rd.get("estado") != null ? (String) rd.get("estado") : "ACTIVA");
                    rutina.setCategoria((String) rd.get("categoria"));
                    rutina.setCreador(rd.get("creador") != null ? (String) rd.get("creador") : "ADMIN");
                    rutina.setEsPlantilla(rd.get("esPlantilla") == null || Boolean.TRUE.equals(rd.get("esPlantilla")));
                    rutina.setProfesor(profesorRestore);
                    rutina.setUsuario(null);
                    rutina = rutinaRepository.save(rutina);
                    rutinasCreadas.add(rutina);
                    rutinasImportadas++;
                }

                for (Map<String, Object> sd : seriesData) {
                    int rutinaIdx = toInt(sd.get("rutinaIndex"), 0);
                    if (rutinaIdx < 0 || rutinaIdx >= rutinasCreadas.size()) continue;
                    Rutina rutina = rutinasCreadas.get(rutinaIdx);
                    if (rutina == null) continue;

                    Serie serie = new Serie();
                    serie.setNombre((String) sd.get("nombre"));
                    serie.setOrden(toInt(sd.get("orden"), 0));
                    serie.setDescripcion((String) sd.get("descripcion"));
                    serie.setEsPlantilla(sd.get("esPlantilla") == null || Boolean.TRUE.equals(sd.get("esPlantilla")));
                    serie.setRepeticionesSerie(toInt(sd.get("repeticionesSerie"), 1));
                    serie.setCreador(sd.get("creador") != null ? (String) sd.get("creador") : "ADMIN");
                    serie.setRutina(rutina);
                    serie.setProfesor(profesorRestore);
                    serie = serieRepository.save(serie);
                    seriesImportadas++;

                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> seList = (List<Map<String, Object>>) sd.get("serieEjercicios");
                    if (seList != null) {
                        int orden = 0;
                        for (Map<String, Object> seMap : seList) {
                            String exerciseName = (String) seMap.get("exerciseName");
                            Exercise ex = ejercicioPorNombre.get(exerciseName);
                            if (ex == null) continue;
                            SerieEjercicio se = new SerieEjercicio();
                            se.setSerie(serie);
                            se.setExercise(ex);
                            se.setValor(toInteger(seMap.get("valor")));
                            se.setUnidad((String) seMap.get("unidad"));
                            se.setPeso(toInteger(seMap.get("peso")));
                            se.setOrden(toInt(seMap.get("orden"), orden));
                            serieEjercicioRepository.save(se);
                            orden++;
                        }
                    }
                }
                logger.info("Restore completo: {} rutinas, {} series (pisarTodos={})", rutinasImportadas, seriesImportadas, pisarTodos);
            }
        }

        result.put("success", true);
        result.put("ejerciciosImportados", importados);
        result.put("ejerciciosOmitidos", omitidos);
        result.put("ejerciciosConImagen", conImagen);
        result.put("rutinasImportadas", rutinasImportadas);
        result.put("seriesImportadas", seriesImportadas);
        if (!errores.isEmpty()) {
            result.put("errores", errores);
        }
        logger.info("Importación ZIP: {} ejercicios, {} rutinas, {} series, pisarTodos={}", importados, rutinasImportadas, seriesImportadas, pisarTodos);
        return result;
    }

    private static int toInt(Object o, int defaultValue) {
        return o instanceof Number ? ((Number) o).intValue() : defaultValue;
    }

    private static Integer toInteger(Object o) {
        if (o == null || !(o instanceof Number)) return null;
        return ((Number) o).intValue();
    }

    private static String extensionDesdeMime(String mime) {
        if (mime == null) return ".jpg";
        if (mime.contains("png")) return ".png";
        if (mime.contains("webp")) return ".webp";
        if (mime.contains("gif")) return ".gif";
        return ".jpg";
    }
}
