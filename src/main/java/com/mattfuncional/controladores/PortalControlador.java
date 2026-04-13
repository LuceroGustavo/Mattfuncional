package com.mattfuncional.controladores;

import com.mattfuncional.entidades.Exercise;
import com.mattfuncional.servicios.ConfiguracionPaginaPublicaService;
import com.mattfuncional.servicios.PlanPublicoService;
import com.mattfuncional.servicios.UsuarioService;
import com.mattfuncional.servicios.ExerciseService;
import com.mattfuncional.servicios.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

@Controller
@RequestMapping("/")
public class PortalControlador {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private ProfesorService profesorService;

    @Autowired
    private PlanPublicoService planPublicoService;

    @Autowired
    private ConfiguracionPaginaPublicaService configuracionPaginaPublicaService;

    private static final String WHATSAPP_TELEFONO_DEFECTO = "5491164842554";

    /** Mensaje prefijado al abrir WhatsApp (app o web) desde la página pública. */
    private static final String WHATSAPP_MENSAJE_CONTACTO =
            "Hola, me gustaría que me contacten para conocer planes y horarios disponibles.";

    /**
     * Enlace oficial: en móvil abre la app; en escritorio abre WhatsApp Web o la app de escritorio si está instalada.
     */
    private String buildWhatsappSendUrl(String whatsappRaw) {
        String digits = "";
        if (whatsappRaw != null && !whatsappRaw.trim().isEmpty()) {
            digits = whatsappRaw.replaceAll("[^0-9]", "");
        }
        if (digits.isEmpty()) {
            digits = WHATSAPP_TELEFONO_DEFECTO;
        }
        String text = URLEncoder.encode(WHATSAPP_MENSAJE_CONTACTO, StandardCharsets.UTF_8);
        return "https://api.whatsapp.com/send?phone=" + digits + "&text=" + text;
    }

    private static final String INSTAGRAM_URL_DEFECTO = "https://www.instagram.com/matt_funcional/";

    /**
     * URL lista para abrir desde la página pública. Acepta URL completa, perfil sin esquema o solo el usuario.
     */
    private String buildInstagramPublicUrl(String instagramRaw) {
        if (instagramRaw == null) {
            return INSTAGRAM_URL_DEFECTO;
        }
        String s = instagramRaw.trim();
        if (s.isEmpty() || "#".equals(s)) {
            return INSTAGRAM_URL_DEFECTO;
        }
        if (s.regionMatches(true, 0, "http://", 0, 7) || s.regionMatches(true, 0, "https://", 0, 8)) {
            return s;
        }
        if (s.regionMatches(true, 0, "//", 0, 2)) {
            return "https:" + s;
        }
        String lower = s.toLowerCase();
        if (lower.contains("instagram.com")) {
            return "https://" + s.replaceFirst("(?i)^https?://", "").replaceFirst("^/+", "");
        }
        /* Usuario: acepta "matt_funcional", "@matt_funcional" o "#matt_funcional" (hashtag → se quita # para la URL del perfil). */
        String user = s.replaceFirst("^[@#]+", "").replaceFirst("^/+", "").replaceFirst("/+$", "");
        if (user.isEmpty()) {
            return INSTAGRAM_URL_DEFECTO;
        }
        return "https://www.instagram.com/" + user + "/";
    }

    /** Página de inicio: landing pública (estilo RedFit). Acceso a la parte privada por ícono de login arriba. */
    @GetMapping("/")
    public String index(Model model) {
        try {
            com.mattfuncional.entidades.Usuario usuarioActual = usuarioService.getUsuarioActual();
            if (usuarioActual != null) {
                model.addAttribute("usuarioActual", usuarioActual);
            }
        } catch (Exception e) {
            // Usuario no autenticado
        }
        String whatsapp = configuracionPaginaPublicaService.getWhatsapp();
        model.addAttribute("whatsappUrl", buildWhatsappSendUrl(whatsapp));
        String instagram = configuracionPaginaPublicaService.getInstagram();
        model.addAttribute("instagramUrl", buildInstagramPublicUrl(instagram));
        return "index-publica";
    }

    /** Ruta alternativa que también muestra la misma página pública (por si se enlaza desde algún lado). */
    @GetMapping("/publica")
    public String indexPublica(Model model) {
        return index(model);
    }

    /** Página de planes (pública). Cards con precios, servicios, días/horarios y formulario de consulta. */
    @GetMapping("/planes")
    public String planes(Model model) {
        model.addAttribute("planes", planPublicoService.getPlanesActivosParaPublica());
        String diasHorarios = configuracionPaginaPublicaService.getDiasHorarios();
        model.addAttribute("diasHorarios", diasHorarios);
        List<String> diasHorariosLineas = new ArrayList<>();
        if (diasHorarios != null && !diasHorarios.isEmpty()) {
            for (String linea : diasHorarios.split("\\r?\\n|\\r")) {
                if (linea != null && !linea.trim().isEmpty()) {
                    diasHorariosLineas.add(linea.trim());
                }
            }
        }
        model.addAttribute("diasHorariosLineas", diasHorariosLineas);
        String direccion = configuracionPaginaPublicaService.getDireccion();
        model.addAttribute("direccion", direccion != null ? direccion : "Aconcagua 17, Ramos Mejía");
        model.addAttribute("direccionUrl", "https://www.google.com/maps/place/Aconcagua+17,+B1704+Ramos+Mej%C3%ADa,+Provincia+de+Buenos+Aires");
        String whatsapp = configuracionPaginaPublicaService.getWhatsapp();
        model.addAttribute("whatsappUrl", buildWhatsappSendUrl(whatsapp));
        String instagram = configuracionPaginaPublicaService.getInstagram();
        model.addAttribute("instagramUrl", buildInstagramPublicUrl(instagram));
        return "planes-publica";
    }

    /* GET /login lo maneja WebMvcConfig (view "login") para mostrar siempre la plantilla Iniciar Sesión. */

    @GetMapping("/demo")
    public String demo() {
        return "demo";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            com.mattfuncional.entidades.Usuario usuarioActual = usuarioService.getUsuarioActual();
            if (usuarioActual != null && ("ADMIN".equals(usuarioActual.getRol()) || "AYUDANTE".equals(usuarioActual.getRol()) || "DEVELOPER".equals(usuarioActual.getRol()))) {
                com.mattfuncional.entidades.Profesor p = usuarioActual.getProfesor() != null ? usuarioActual.getProfesor() : profesorService.getProfesorByCorreo(usuarioActual.getCorreo());
                if (p == null && "DEVELOPER".equals(usuarioActual.getRol())) {
                    p = profesorService.getProfesorByCorreo("profesor@mattfuncional.com");
                }
                if (p != null) return "redirect:/profesor/" + p.getId();
                return "redirect:/profesor/dashboard";
            }
        } catch (Exception e) {
            // Usuario no autenticado
        }
        
        // Fallback: redirigir a la página principal
        return "redirect:/";
    }

    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            // Verificar profesor (único gestor del panel)
            com.mattfuncional.entidades.Profesor profesorPrincipal = profesorService.getProfesorByCorreo("profesor@mattfuncional.com");
            status.put("profesorPrincipalExiste", profesorPrincipal != null);
            if (profesorPrincipal != null) {
                status.put("profesorPrincipalId", profesorPrincipal.getId());
                status.put("profesorPrincipalNombre", profesorPrincipal.getNombre());
                List<Exercise> ejerciciosProfesor = exerciseService.findExercisesByProfesorId(profesorPrincipal.getId());
                status.put("ejerciciosProfesorCount", ejerciciosProfesor.size());
            }
            
            // Verificar archivos estáticos
            try {
                Resource logoResource = new ClassPathResource("static/img/logo.png");
                status.put("logoExiste", logoResource.exists());
                status.put("logoPath", "/img/logo.png");
            } catch (Exception e) {
                status.put("logoExiste", false);
                status.put("logoError", e.getMessage());
            }
            
            status.put("status", "OK");
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            status.put("status", "ERROR");
            status.put("error", e.getMessage());
            return ResponseEntity.status(500).body(status);
        }
    }
}
