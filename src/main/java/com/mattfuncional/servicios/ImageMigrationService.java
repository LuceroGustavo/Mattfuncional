package com.mattfuncional.servicios;

import com.mattfuncional.entidades.Imagen;
import com.mattfuncional.repositorios.ImagenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class ImageMigrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ImageMigrationService.class);
    
    private final ImagenRepository imagenRepository;
    private final ImageOptimizationService imageOptimizationService;
    private final ImagenServicio imagenServicio;
    
    public ImageMigrationService(ImagenRepository imagenRepository, 
                               ImageOptimizationService imageOptimizationService,
                               ImagenServicio imagenServicio) {
        this.imagenRepository = imagenRepository;
        this.imageOptimizationService = imageOptimizationService;
        this.imagenServicio = imagenServicio;
    }
    
    /**
     * Migra todas las imágenes existentes a WebP (opcional)
     * @return Número de imágenes migradas exitosamente
     */
    public int migrarImagenesAWebP() {
        logger.info("=== Iniciando migración de imágenes a WebP ===");
        
        if (!imageOptimizationService.isWebPSupported()) {
            logger.warn("WebP no está soportado en este sistema. Migración cancelada.");
            return 0;
        }
        
        List<Imagen> todasLasImagenes = imagenRepository.findAll();
        logger.info("Total de imágenes encontradas: {}", todasLasImagenes.size());
        
        AtomicInteger migradas = new AtomicInteger(0);
        AtomicInteger noMigradas = new AtomicInteger(0);
        
        todasLasImagenes.parallelStream().forEach(imagen -> {
            try {
                if (migrarImagenIndividual(imagen)) {
                    migradas.incrementAndGet();
                } else {
                    noMigradas.incrementAndGet();
                }
            } catch (Exception e) {
                logger.error("Error migrando imagen {}: {}", imagen.getId(), e.getMessage());
                noMigradas.incrementAndGet();
            }
        });
        
        logger.info("=== Migración completada ===");
        logger.info("Imágenes migradas a WebP: {}", migradas.get());
        logger.info("Imágenes no migradas: {}", noMigradas.get());
        
        return migradas.get();
    }
    
    /**
     * Migra una imagen individual a WebP si es posible
     * @param imagen Imagen a migrar
     * @return true si se migró exitosamente, false en caso contrario
     */
    private boolean migrarImagenIndividual(Imagen imagen) {
        try {
            // Verificar si ya es WebP
            if ("image/webp".equals(imagen.getMime())) {
                logger.debug("Imagen {} ya es WebP, saltando", imagen.getId());
                return false;
            }
            
            // Verificar si es compatible con conversión a WebP
            String formatoOriginal = getFormatoFromMime(imagen.getMime());
            if (!imageOptimizationService.isCompatibleWithWebP(formatoOriginal)) {
                logger.debug("Imagen {} no es compatible con conversión a WebP", imagen.getId());
                return false;
            }
            
            // Obtener contenido del archivo físico
            byte[] contenidoOriginal = imagenServicio.obtenerContenido(imagen.getId());
            byte[] contenidoWebP = imageOptimizationService.optimizeImage(contenidoOriginal, formatoOriginal);
            
            // Verificar si la conversión fue exitosa
            if (contenidoWebP != null && contenidoWebP.length > 0) {
                // Verificar si realmente se convirtió a WebP
                if (isValidWebP(contenidoWebP)) {
                    // Actualizar archivo físico y metadatos
                    String nombreWebP = generarNombreWebP(imagen.getNombre());
                    
                    // Eliminar archivo físico antiguo
                    try {
                        java.nio.file.Path archivoAntiguo = java.nio.file.Paths.get("uploads", "ejercicios", imagen.getRutaArchivo());
                        if (java.nio.file.Files.exists(archivoAntiguo)) {
                            java.nio.file.Files.delete(archivoAntiguo);
                        }
                    } catch (Exception e) {
                        logger.warn("No se pudo eliminar archivo antiguo: {}", e.getMessage());
                    }
                    
                    // Guardar nuevo archivo WebP
                    Imagen nuevaImagen = imagenServicio.guardar(contenidoWebP, nombreWebP);
                    
                    // Actualizar metadatos manteniendo el mismo ID
                    imagen.setMime("image/webp");
                    imagen.setNombre(nombreWebP);
                    imagen.setRutaArchivo(nuevaImagen.getRutaArchivo());
                    imagen.setTamanoBytes(nuevaImagen.getTamanoBytes());
                    imagenRepository.save(imagen);
                    
                    // Eliminar la imagen temporal creada (solo se necesitaba para obtener la ruta)
                    if (!nuevaImagen.getId().equals(imagen.getId())) {
                        imagenRepository.delete(nuevaImagen);
                    }
                    
                    logger.info("Imagen {} migrada exitosamente: {} -> WebP ({} bytes -> {} bytes)", 
                        imagen.getId(), formatoOriginal, contenidoOriginal.length, contenidoWebP.length);
                    
                    return true;
                }
            }
            
            logger.debug("Imagen {} no se pudo convertir a WebP", imagen.getId());
            return false;
            
        } catch (Exception e) {
            logger.error("Error migrando imagen {}: {}", imagen.getId(), e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene el formato de imagen desde el MIME type
     */
    private String getFormatoFromMime(String mimeType) {
        if (mimeType == null) return "jpg";
        
        switch (mimeType.toLowerCase()) {
            case "image/png": return "png";
            case "image/gif": return "gif";
            case "image/bmp": return "bmp";
            case "image/webp": return "webp";
            case "image/jpg":
            case "image/jpeg": return "jpg";
            default: return "jpg";
        }
    }
    
    /**
     * Valida que el archivo WebP generado sea válido
     */
    private boolean isValidWebP(byte[] webpData) {
        if (webpData.length < 12) return false;
        
        // Verificar firma WebP: RIFF....WEBP
        return webpData[0] == 'R' && webpData[1] == 'I' && 
               webpData[2] == 'F' && webpData[3] == 'F' &&
               webpData[8] == 'W' && webpData[9] == 'E' && 
               webpData[10] == 'B' && webpData[11] == 'P';
    }
    
    /**
     * Genera un nombre de archivo WebP
     */
    private String generarNombreWebP(String nombreOriginal) {
        if (nombreOriginal == null) {
            return "imagen.webp";
        }
        
        // Remover extensión original y agregar .webp
        String nombreSinExtension = nombreOriginal;
        int lastDotIndex = nombreOriginal.lastIndexOf(".");
        if (lastDotIndex > 0) {
            nombreSinExtension = nombreOriginal.substring(0, lastDotIndex);
        }
        
        return nombreSinExtension + ".webp";
    }
    
    /**
     * Obtiene estadísticas de migración
     */
    public String obtenerEstadisticasMigracion() {
        List<Imagen> todasLasImagenes = imagenRepository.findAll();
        
        long totalImagenes = todasLasImagenes.size();
        long imagenesWebP = todasLasImagenes.stream()
            .filter(img -> "image/webp".equals(img.getMime()))
            .count();
        long imagenesGif = todasLasImagenes.stream()
            .filter(img -> "image/gif".equals(img.getMime()))
            .count();
        long imagenesPng = todasLasImagenes.stream()
            .filter(img -> "image/png".equals(img.getMime()))
            .count();
        long imagenesJpg = todasLasImagenes.stream()
            .filter(img -> "image/jpeg".equals(img.getMime()) || "image/jpg".equals(img.getMime()))
            .count();
        
        return String.format(
            "Total: %d | WebP: %d | GIF: %d | PNG: %d | JPG: %d | WebP: %.1f%%",
            totalImagenes, imagenesWebP, imagenesGif, imagenesPng, imagenesJpg,
            totalImagenes > 0 ? (imagenesWebP * 100.0 / totalImagenes) : 0.0
        );
    }
}
