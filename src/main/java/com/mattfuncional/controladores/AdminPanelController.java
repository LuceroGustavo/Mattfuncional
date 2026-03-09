package com.mattfuncional.controladores;

import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.servicios.ExerciseBackupService;
import com.mattfuncional.servicios.ExerciseExportImportService;
import com.mattfuncional.servicios.ExerciseZipBackupService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/profesor")
public class AdminPanelController {

    private static final DateTimeFormatter ZIP_FILENAME_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

    private final ExerciseExportImportService exerciseExportImportService;
    private final ExerciseBackupService exerciseBackupService;
    private final ExerciseZipBackupService exerciseZipBackupService;

    public AdminPanelController(ExerciseExportImportService exerciseExportImportService,
                                ExerciseBackupService exerciseBackupService,
                                ExerciseZipBackupService exerciseZipBackupService) {
        this.exerciseExportImportService = exerciseExportImportService;
        this.exerciseBackupService = exerciseBackupService;
        this.exerciseZipBackupService = exerciseZipBackupService;
    }

    @GetMapping("/administracion")
    public String panelAdministracion(@AuthenticationPrincipal Usuario usuarioActual) {
        if (usuarioActual == null || (!"ADMIN".equals(usuarioActual.getRol()) && !"DEVELOPER".equals(usuarioActual.getRol()))) {
            return "redirect:/profesor/dashboard";
        }
        return "profesor/administracion";
    }

    @GetMapping("/backup")
    public String paginaBackup(@AuthenticationPrincipal Usuario usuarioActual, Model model) {
        if (usuarioActual == null || (!"ADMIN".equals(usuarioActual.getRol()) && !"DEVELOPER".equals(usuarioActual.getRol()))) {
            return "redirect:/profesor/dashboard";
        }
        Map<String, Object> backupsResult = exerciseExportImportService.listarBackupsDisponibles();
        Map<String, Object> profesoresResult = exerciseBackupService.obtenerProfesoresParaBackup();
        model.addAttribute("backups", backupsResult.get("backups"));
        model.addAttribute("totalBackups", backupsResult.get("total") != null ? backupsResult.get("total") : 0);
        model.addAttribute("backupDirectory", backupsResult.get("backupDirectory"));
        model.addAttribute("profesores", profesoresResult.get("profesores"));
        return "profesor/backup";
    }

    /**
     * Exporta todos los ejercicios del sistema a ZIP (ejercicios.json + carpeta imagenes/).
     */
    @GetMapping("/backup/exportar-zip")
    public ResponseEntity<Resource> exportarEjerciciosZip(@AuthenticationPrincipal Usuario usuarioActual) {
        if (usuarioActual == null || (!"ADMIN".equals(usuarioActual.getRol()) && !"DEVELOPER".equals(usuarioActual.getRol()))) {
            return ResponseEntity.notFound().build();
        }
        try {
            byte[] zipBytes = exerciseZipBackupService.exportarEjerciciosAZip();
            String fileName = "ejercicios_backup_" + LocalDateTime.now().format(ZIP_FILENAME_DATE) + ".zip";
            Resource resource = new ByteArrayResource(zipBytes);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Exporta ejercicios a JSON (con imágenes en Base64). Alternativa al ZIP.
     * profesorId=null → ejercicios predeterminados del sistema.
     * profesorId no null → ejercicios creados por ese profesor.
     */
    @GetMapping("/backup/exportar")
    public ResponseEntity<Resource> exportarEjercicios(@AuthenticationPrincipal Usuario usuarioActual,
                                                        @RequestParam(required = false) Long profesorId) {
        if (usuarioActual == null || (!"ADMIN".equals(usuarioActual.getRol()) && !"DEVELOPER".equals(usuarioActual.getRol()))) {
            return ResponseEntity.notFound().build();
        }
        try {
            Map<String, Object> result = exerciseExportImportService.exportarEjerciciosProfesor(profesorId);
            if (!Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.badRequest().build();
            }
            String fileName = (String) result.get("fileName");
            if (fileName == null || fileName.contains("..")) {
                return ResponseEntity.badRequest().build();
            }
            Path dir = Paths.get("").toAbsolutePath().resolve("backups").resolve("ejercicios");
            Path path = dir.resolve(fileName).normalize();
            if (!path.startsWith(dir)) {
                return ResponseEntity.badRequest().build();
            }
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Descarga un archivo de backup existente por nombre.
     */
    @GetMapping("/backup/descargar")
    public ResponseEntity<Resource> descargarBackup(@AuthenticationPrincipal Usuario usuarioActual,
                                                    @RequestParam String fileName) {
        if (usuarioActual == null || (!"ADMIN".equals(usuarioActual.getRol()) && !"DEVELOPER".equals(usuarioActual.getRol()))) {
            return ResponseEntity.notFound().build();
        }
        if (fileName == null || fileName.contains("..") || !fileName.endsWith(".json")) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Path path = Paths.get("").toAbsolutePath().resolve("backups").resolve("ejercicios").resolve(fileName);
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Importa ejercicios desde un archivo ZIP subido (arrastrar o seleccionar archivo).
     * No usa la carpeta backup del servidor: el backup viene del usuario.
     */
    @PostMapping("/backup/importar")
    public String importarBackupZip(@AuthenticationPrincipal Usuario usuarioActual,
                                    @RequestParam("archivoZip") MultipartFile archivoZip,
                                    @RequestParam(value = "pisarTodos", required = false) boolean pisarTodos,
                                    Model model) {
        if (usuarioActual == null || (!"ADMIN".equals(usuarioActual.getRol()) && !"DEVELOPER".equals(usuarioActual.getRol()))) {
            return "redirect:/profesor/dashboard";
        }
        try {
            Map<String, Object> result = exerciseZipBackupService.importarDesdeZip(archivoZip, pisarTodos);
            model.addAttribute("importResult", result);
        } catch (IOException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Error al leer el archivo: " + e.getMessage());
            model.addAttribute("importResult", err);
        }
        Map<String, Object> backupsResult = exerciseExportImportService.listarBackupsDisponibles();
        Map<String, Object> profesoresResult = exerciseBackupService.obtenerProfesoresParaBackup();
        model.addAttribute("backups", backupsResult.get("backups"));
        model.addAttribute("totalBackups", backupsResult.get("total") != null ? backupsResult.get("total") : 0);
        model.addAttribute("backupDirectory", backupsResult.get("backupDirectory"));
        model.addAttribute("profesores", profesoresResult.get("profesores"));
        return "profesor/backup";
    }
}
