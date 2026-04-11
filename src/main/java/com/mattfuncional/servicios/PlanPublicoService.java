package com.mattfuncional.servicios;

import com.mattfuncional.config.MattUploadsPathResolver;
import com.mattfuncional.entidades.PlanPublico;
import com.mattfuncional.enums.TipoPlanPublico;
import com.mattfuncional.repositorios.PlanPublicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class PlanPublicoService {

    private static final long MAX_PROMO_IMAGE_BYTES = 5 * 1024 * 1024;

    @Autowired
    private PlanPublicoRepository planPublicoRepository;

    @Autowired
    private MattUploadsPathResolver uploadsPathResolver;

    /** Actualiza filas antiguas sin tipo tras agregar el campo en la entidad. */
    @Transactional
    public void migrarTipoNuloPlanes() {
        try {
            planPublicoRepository.setDefaultTipoWhereNull(TipoPlanPublico.PLAN);
        } catch (Exception ignored) {
            // Columna nueva aún no creada en esta ejecución
        }
    }

    public List<PlanPublico> getPlanesActivosParaPublica() {
        return planPublicoRepository.findByActivoTrueOrderByOrdenAsc();
    }

    public List<PlanPublico> getAllPlanes() {
        return planPublicoRepository.findAllByOrderByOrdenAsc();
    }

    public PlanPublico getById(Long id) {
        return planPublicoRepository.findById(id).orElse(null);
    }

    /** Valor de orden para un plan nuevo: siempre al final de la lista (las flechas ajustan después). */
    @Transactional(readOnly = true)
    public int siguienteOrdenAlFinal() {
        Integer max = planPublicoRepository.findMaxOrden();
        return max == null ? 0 : max + 1;
    }

    @Transactional
    public PlanPublico guardar(PlanPublico plan) {
        return planPublicoRepository.save(plan);
    }

    @Transactional
    public void eliminar(Long id) {
        PlanPublico p = planPublicoRepository.findById(id).orElse(null);
        if (p != null && p.getRutaImagen() != null && !p.getRutaImagen().isBlank()) {
            borrarArchivoPromocion(p.getRutaImagen());
        }
        planPublicoRepository.deleteById(id);
    }

    /** Guarda imagen de promoción y devuelve URL pública (/media/promociones/...). */
    public String guardarImagenPromocion(MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }
        if (archivo.getSize() > MAX_PROMO_IMAGE_BYTES) {
            throw new IllegalArgumentException("La imagen supera 5 MB.");
        }
        String original = archivo.getOriginalFilename();
        String ext = extensionSegura(original);
        if (ext == null) {
            throw new IllegalArgumentException("Formato de imagen no permitido (usá JPG, PNG, WebP o GIF). Desde el iPhone: elegí «Más compatible» o convertí HEIC a JPG antes de subir.");
        }
        Path dir = uploadsPathResolver.getRoot().resolve("promociones-publicas");
        Files.createDirectories(dir);
        String nombre = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path destino = dir.resolve(nombre);
        try (InputStream in = archivo.getInputStream()) {
            Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
        }
        return "/media/promociones/" + nombre;
    }

    private String extensionSegura(String nombre) {
        if (nombre == null) return null;
        int dot = nombre.lastIndexOf('.');
        if (dot < 0 || dot == nombre.length() - 1) return null;
        String e = nombre.substring(dot + 1).toLowerCase(Locale.ROOT);
        return switch (e) {
            case "jpg", "jpeg", "png", "gif", "webp" -> e.equals("jpeg") ? "jpg" : e;
            default -> null;
        };
    }

    public void borrarArchivoPromocion(String rutaPublica) {
        if (rutaPublica == null || !rutaPublica.startsWith("/media/promociones/")) {
            return;
        }
        String file = rutaPublica.substring("/media/promociones/".length());
        if (file.contains("..") || file.contains("/") || file.contains("\\")) {
            return;
        }
        try {
            Path path = uploadsPathResolver.getRoot().resolve("promociones-publicas").resolve(file);
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    /** Mueve el plan una posición hacia arriba (menor orden). Retorna true si se movió. */
    @Transactional
    public boolean moverArriba(Long id) {
        List<PlanPublico> planes = planPublicoRepository.findAllByOrderByOrdenAsc();
        int idx = -1;
        for (int i = 0; i < planes.size(); i++) {
            if (planes.get(i).getId().equals(id)) {
                idx = i;
                break;
            }
        }
        if (idx <= 0) return false;
        PlanPublico actual = planes.get(idx);
        PlanPublico anterior = planes.get(idx - 1);
        int ordActual = actual.getOrden();
        actual.setOrden(anterior.getOrden());
        anterior.setOrden(ordActual);
        planPublicoRepository.save(actual);
        planPublicoRepository.save(anterior);
        return true;
    }

    /** Mueve el plan una posición hacia abajo (mayor orden). Retorna true si se movió. */
    @Transactional
    public boolean moverAbajo(Long id) {
        List<PlanPublico> planes = planPublicoRepository.findAllByOrderByOrdenAsc();
        int idx = -1;
        for (int i = 0; i < planes.size(); i++) {
            if (planes.get(i).getId().equals(id)) {
                idx = i;
                break;
            }
        }
        if (idx < 0 || idx >= planes.size() - 1) return false;
        PlanPublico actual = planes.get(idx);
        PlanPublico siguiente = planes.get(idx + 1);
        int ordActual = actual.getOrden();
        actual.setOrden(siguiente.getOrden());
        siguiente.setOrden(ordActual);
        planPublicoRepository.save(actual);
        planPublicoRepository.save(siguiente);
        return true;
    }

    /** Crea los 4 planes iniciales si no existen. */
    @Transactional
    public void asegurarPlanesIniciales() {
        if (planPublicoRepository.count() > 0) {
            return;
        }
        crearPlan("1 vez por semana", "Acceso una vez por semana.", 15000.0, 1, 0);
        crearPlan("2 veces por semana", "Acceso dos veces por semana.", 25000.0, 2, 1);
        crearPlan("3 veces por semana", "Acceso tres veces por semana.", 35000.0, 3, 2);
        crearPlan("Opción libre", "Acceso libre sin restricción de días.", 45000.0, null, 3);
    }

    private void crearPlan(String nombre, String descripcion, Double precio, Integer vecesPorSemana, int orden) {
        PlanPublico p = new PlanPublico();
        p.setTipo(TipoPlanPublico.PLAN);
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio);
        p.setVecesPorSemana(vecesPorSemana);
        p.setOrden(orden);
        p.setActivo(true);
        planPublicoRepository.save(p);
    }
}
