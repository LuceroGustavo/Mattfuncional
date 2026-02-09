package com.mattfuncional.controladores;

import com.mattfuncional.servicios.ImageMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/images")
@PreAuthorize("hasRole('ADMIN')")
public class ImageMigrationController {
    
    private static final Logger logger = LoggerFactory.getLogger(ImageMigrationController.class);
    
    private final ImageMigrationService imageMigrationService;
    
    @Autowired
    public ImageMigrationController(ImageMigrationService imageMigrationService) {
        this.imageMigrationService = imageMigrationService;
    }
    
    /**
     * Obtiene estadísticas actuales de las imágenes
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getImageStats() {
        try {
            String stats = imageMigrationService.obtenerEstadisticasMigracion();
            
            Map<String, Object> response = new HashMap<>();
            response.put("stats", stats);
            response.put("message", "Estadísticas obtenidas exitosamente");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas de imágenes: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error obteniendo estadísticas");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Inicia la migración de imágenes a WebP
     */
    @PostMapping("/migrate")
    public ResponseEntity<Map<String, Object>> migrateImagesToWebP() {
        try {
            logger.info("Iniciando migración de imágenes a WebP...");
            
            int imagenesMigradas = imageMigrationService.migrarImagenesAWebP();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Migración completada exitosamente");
            response.put("imagenesMigradas", imagenesMigradas);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("Migración completada: {} imágenes migradas", imagenesMigradas);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error durante la migración de imágenes: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error durante la migración");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Verifica el estado del sistema de imágenes
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        try {
            String stats = imageMigrationService.obtenerEstadisticasMigracion();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "OK");
            response.put("stats", stats);
            response.put("webpSupported", true); // Asumiendo que WebP está soportado
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error verificando estado del sistema: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("error", "Error verificando estado del sistema");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
