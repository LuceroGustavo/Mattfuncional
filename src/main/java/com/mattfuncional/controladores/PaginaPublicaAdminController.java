package com.mattfuncional.controladores;

import com.mattfuncional.config.MattUploadsPathResolver;
import com.mattfuncional.entidades.ConfiguracionPaginaPublica;
import com.mattfuncional.entidades.PlanPublico;
import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.enums.TipoPlanPublico;
import com.mattfuncional.servicios.ConfiguracionPaginaPublicaService;
import com.mattfuncional.servicios.ConsultaService;
import com.mattfuncional.servicios.PlanPublicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;


@Controller
@RequestMapping("/profesor/pagina-publica")
public class PaginaPublicaAdminController {

    @Autowired
    private PlanPublicoService planPublicoService;

    @Autowired
    private ConfiguracionPaginaPublicaService configuracionPaginaPublicaService;

    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private MattUploadsPathResolver uploadsPathResolver;

    @GetMapping
    public String verPaginaPublica(@AuthenticationPrincipal Usuario usuarioActual,
                                   @RequestParam(name = "fragment", required = false) String fragment,
                                   Model model) {
        if (usuarioActual == null || !isAdminOrDeveloper(usuarioActual)) {
            return "redirect:/profesor/dashboard";
        }
        model.addAttribute("planes", planPublicoService.getAllPlanes());
        model.addAttribute("config", configuracionPaginaPublicaService.getAllConfig());
        model.addAttribute("consultas", consultaService.getUltimasConsultas(20));
        model.addAttribute("rutaPromocionesEnDisco", uploadsPathResolver.getRoot().resolve("promociones-publicas").toString());
        if (fragment != null && !fragment.isEmpty()) {
            return "profesor/pagina-publica-admin :: contenido";
        }
        return "redirect:/profesor/administracion?seccion=pagina";
    }

    @PostMapping("/plan/guardar")
    public String guardarPlan(@AuthenticationPrincipal Usuario usuarioActual,
                              @RequestParam(required = false) Long id,
                              @RequestParam String nombre,
                              @RequestParam(required = false) String descripcion,
                              @RequestParam(required = false) String precioEtiqueta,
                              @RequestParam(required = false) Integer vecesPorSemana,
                              @RequestParam(defaultValue = "PLAN") String tipo,
                              @RequestParam(required = false) MultipartFile imagen,
                              @RequestParam(required = false) String quitarImagen,
                              RedirectAttributes ra) {
        if (usuarioActual == null || !isAdminOrDeveloper(usuarioActual)) {
            return "redirect:/profesor/dashboard";
        }
        TipoPlanPublico tipoEnum;
        try {
            tipoEnum = TipoPlanPublico.valueOf(tipo != null ? tipo.trim().toUpperCase() : "PLAN");
        } catch (IllegalArgumentException e) {
            tipoEnum = TipoPlanPublico.PLAN;
        }

        PlanPublico plan = id != null ? planPublicoService.getById(id) : new PlanPublico();
        if (plan == null) {
            plan = new PlanPublico();
        }
        String imagenAnterior = plan.getRutaImagen();

        plan.setNombre(nombre);
        plan.setDescripcion(descripcion != null ? descripcion : "");
        plan.setActivo(true);
        plan.setTipo(tipoEnum);
        aplicarPrecioEtiqueta(plan, precioEtiqueta);
        if (plan.getId() == null) {
            plan.setOrden(planPublicoService.siguienteOrdenAlFinal());
        }

        try {
            if (tipoEnum == TipoPlanPublico.PLAN) {
                plan.setVecesPorSemana(vecesPorSemana);
                if (imagenAnterior != null && !imagenAnterior.isBlank()) {
                    planPublicoService.borrarArchivoPromocion(imagenAnterior);
                    plan.setRutaImagen(null);
                }
            } else {
                plan.setVecesPorSemana(null);
                boolean quitar = "1".equals(quitarImagen) || "on".equalsIgnoreCase(String.valueOf(quitarImagen));
                if (quitar) {
                    if (imagenAnterior != null && !imagenAnterior.isBlank()) {
                        planPublicoService.borrarArchivoPromocion(imagenAnterior);
                    }
                    plan.setRutaImagen(null);
                }
                if (imagen != null && !imagen.isEmpty()) {
                    if (plan.getRutaImagen() != null && !plan.getRutaImagen().isBlank()) {
                        planPublicoService.borrarArchivoPromocion(plan.getRutaImagen());
                    }
                    plan.setRutaImagen(planPublicoService.guardarImagenPromocion(imagen));
                }
            }
            planPublicoService.guardar(plan);
        } catch (IllegalArgumentException | IOException e) {
            ra.addFlashAttribute("error", e.getMessage() != null ? e.getMessage() : "No se pudo guardar.");
            return "redirect:/profesor/administracion?seccion=pagina";
        }

        ra.addFlashAttribute("ok", "Guardado correctamente");
        return "redirect:/profesor/administracion?seccion=pagina";
    }

    @PostMapping("/plan/eliminar")
    public String eliminarPlan(@AuthenticationPrincipal Usuario usuarioActual,
                              @RequestParam Long id,
                              RedirectAttributes ra) {
        if (usuarioActual == null || !isAdminOrDeveloper(usuarioActual)) {
            return "redirect:/profesor/dashboard";
        }
        planPublicoService.eliminar(id);
        ra.addFlashAttribute("ok", "Plan eliminado");
        return "redirect:/profesor/administracion?seccion=pagina";
    }

    @PostMapping("/plan/subir/{id}")
    public String subirPlan(@AuthenticationPrincipal Usuario usuarioActual,
                           @PathVariable Long id,
                           RedirectAttributes ra) {
        if (usuarioActual == null || !isAdminOrDeveloper(usuarioActual)) {
            return "redirect:/profesor/dashboard";
        }
        planPublicoService.moverArriba(id);
        ra.addFlashAttribute("ok", "Orden actualizado");
        return "redirect:/profesor/administracion?seccion=pagina";
    }

    @PostMapping("/plan/bajar/{id}")
    public String bajarPlan(@AuthenticationPrincipal Usuario usuarioActual,
                           @PathVariable Long id,
                           RedirectAttributes ra) {
        if (usuarioActual == null || !isAdminOrDeveloper(usuarioActual)) {
            return "redirect:/profesor/dashboard";
        }
        planPublicoService.moverAbajo(id);
        ra.addFlashAttribute("ok", "Orden actualizado");
        return "redirect:/profesor/administracion?seccion=pagina";
    }

    @PostMapping("/consulta/eliminar")
    public String eliminarConsulta(@AuthenticationPrincipal Usuario usuarioActual,
                                   @RequestParam Long id,
                                   RedirectAttributes ra) {
        if (usuarioActual == null || !isAdminOrDeveloper(usuarioActual)) {
            return "redirect:/profesor/dashboard";
        }
        consultaService.eliminar(id);
        ra.addFlashAttribute("ok", "Consulta eliminada");
        return "redirect:/profesor/administracion?seccion=pagina";
    }

    @PostMapping("/config")
    public String guardarConfig(@AuthenticationPrincipal Usuario usuarioActual,
                                @RequestParam(required = false) String whatsapp,
                                @RequestParam(required = false) String instagram,
                                @RequestParam(required = false) String direccion,
                                @RequestParam(required = false) String dias_horarios,
                                @RequestParam(required = false) String telefono,
                                RedirectAttributes ra) {
        if (usuarioActual == null || !isAdminOrDeveloper(usuarioActual)) {
            return "redirect:/profesor/dashboard";
        }
        configuracionPaginaPublicaService.actualizar(ConfiguracionPaginaPublica.CLAVE_WHATSAPP, whatsapp);
        configuracionPaginaPublicaService.actualizar(ConfiguracionPaginaPublica.CLAVE_INSTAGRAM, instagram);
        configuracionPaginaPublicaService.actualizar(ConfiguracionPaginaPublica.CLAVE_DIRECCION, direccion);
        configuracionPaginaPublicaService.actualizar(ConfiguracionPaginaPublica.CLAVE_DIAS_HORARIOS, dias_horarios);
        configuracionPaginaPublicaService.actualizar(ConfiguracionPaginaPublica.CLAVE_TELEFONO, telefono);
        ra.addFlashAttribute("ok", "Configuración guardada");
        return "redirect:/profesor/administracion?seccion=pagina";
    }

    @PostMapping("/consulta/marcar-visto/{id}")
    public String marcarConsultaVistoPost(@AuthenticationPrincipal Usuario usuarioActual,
                                          @PathVariable Long id,
                                          RedirectAttributes ra) {
        return marcarConsultaVisto(usuarioActual, id, ra);
    }

    @GetMapping("/consulta/marcar-visto/{id}")
    public String marcarConsultaVistoGet(@AuthenticationPrincipal Usuario usuarioActual,
                                         @PathVariable Long id,
                                         RedirectAttributes ra) {
        return marcarConsultaVisto(usuarioActual, id, ra);
    }

    private String marcarConsultaVisto(Usuario usuarioActual, Long id, RedirectAttributes ra) {
        if (usuarioActual == null || !isAdminOrDeveloper(usuarioActual)) {
            return "redirect:/profesor/dashboard";
        }
        consultaService.marcarComoVisto(id);
        ra.addFlashAttribute("ok", "Consulta marcada como vista");
        return "redirect:/profesor/administracion?seccion=pagina";
    }

    private boolean isAdminOrDeveloper(Usuario u) {
        return u != null && ("ADMIN".equals(u.getRol()) || "DEVELOPER".equals(u.getRol()));
    }

    /** Guarda texto libre (Gratis, consultar, $15.000) y un valor numérico aproximado para ordenar / compatibilidad. */
    private static void aplicarPrecioEtiqueta(PlanPublico plan, String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            plan.setPrecioEtiqueta(null);
            plan.setPrecio(0d);
            return;
        }
        String etiqueta = raw.trim();
        plan.setPrecioEtiqueta(etiqueta);
        plan.setPrecio(parsePrecioNumerico(etiqueta));
    }

    private static double parsePrecioNumerico(String etiqueta) {
        if (etiqueta == null || etiqueta.isBlank()) {
            return 0d;
        }
        String t = etiqueta.trim();
        try {
            return Double.parseDouble(t.replace(',', '.'));
        } catch (NumberFormatException ignored) {
        }
        String soloDigitos = t.replaceAll("[^0-9]", "");
        if (soloDigitos.isEmpty()) {
            return 0d;
        }
        try {
            return Double.parseDouble(soloDigitos);
        } catch (NumberFormatException e) {
            return 0d;
        }
    }
}
