package com.mattfuncional.controladores;

import com.mattfuncional.entidades.Rutina;
import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.entidades.Serie;
import com.mattfuncional.servicios.RutinaService;
import com.mattfuncional.servicios.UsuarioService;
import com.mattfuncional.servicios.SerieService;
import com.mattfuncional.excepciones.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;

@Controller
@RequestMapping("/rutinas")
public class RutinaControlador {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RutinaService rutinaService;

    @Autowired
    private SerieService serieService;

    // GET: Mostrar formulario de creación de rutina plantilla
    @GetMapping("/crear")
    public String crearRutina(Model model) {
        Usuario usuarioActual = usuarioService.getUsuarioActual();
        if (usuarioActual == null || usuarioActual.getProfesor() == null) {
            // Si no es un profesor, no debería estar aquí. Redirigir.
            return "redirect:/login";
        }

        // Cargar las series plantilla del profesor logueado
        Long profesorId = usuarioActual.getProfesor().getId();
        List<Serie> seriesDelProfesor = serieService.findByProfesorId(profesorId);

        // Filtrar en Java para obtener solo las plantillas
        List<Serie> seriesPlantilla = seriesDelProfesor.stream()
                .filter(Serie::isEsPlantilla)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("seriesPlantilla", seriesPlantilla);
        model.addAttribute("rutina", new Rutina());
        model.addAttribute("usuario", usuarioActual);

        return "rutinas/crearRutina";
    }

    // POST: Crear rutina plantilla
    @PostMapping("/crear-plantilla")
    public String crearRutinaPlantilla(@RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam String categoria,
            @RequestParam Long profesorId,
            @RequestParam(required = false) List<Long> selectedSeries,
            @RequestParam Map<String, String> allParams,
            Model model) {
        try {
            Rutina rutina = rutinaService.crearRutinaPlantilla(profesorId, nombre, descripcion, categoria);

            // Si hay series seleccionadas, agregarlas a la rutina con sus repeticiones
            if (selectedSeries != null && !selectedSeries.isEmpty()) {
                for (Long serieId : selectedSeries) {
                    // Obtener las repeticiones para esta serie
                    String repeticionesKey = "repeticiones_" + serieId;
                    int repeticiones = 1; // valor por defecto
                    if (allParams.containsKey(repeticionesKey)) {
                        try {
                            repeticiones = Integer.parseInt(allParams.get(repeticionesKey));
                        } catch (NumberFormatException e) {
                            // Si no se puede parsear, usar valor por defecto
                        }
                    }

                    // Agregar la serie a la rutina con sus repeticiones
                    rutinaService.agregarSerieARutina(rutina.getId(), serieId, repeticiones);
                }
            }

            return "redirect:/profesor/dashboard?success=Rutina creada exitosamente";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al crear la rutina: " + e.getMessage());
            return "redirect:/rutinas/crear";
        }
    }







    // GET: Editar rutina
    @GetMapping("/editar/{id}")
    public String editarRutina(@PathVariable Long id, Model model) {
        try {
            // Obtener la rutina a editar
            Rutina rutina = rutinaService.obtenerRutinaPorId(id);

            // Obtener el profesor actual
            Usuario usuarioActual = usuarioService.getUsuarioActual();
            if (usuarioActual == null || usuarioActual.getProfesor() == null) {
                return "redirect:/login";
            }
            Long profesorId = usuarioActual.getProfesor().getId();

            // Verificar que el profesor sea el dueño de la rutina
            if (!rutina.getProfesor().getId().equals(profesorId)) {
                return "redirect:/profesor/dashboard?error=No tiene permiso para editar esta rutina";
            }

            // Obtener todas las series plantilla del profesor
            List<Serie> todasLasSeriesPlantilla = serieService.obtenerSeriesPlantillaPorProfesor(profesorId);

            // Obtener los IDs de las plantillas de series que ya están en la rutina
            Set<Long> idsSeriesEnRutina = rutina.getSeries().stream()
                    .map(Serie::getPlantillaId)
                    .collect(Collectors.toSet());

            // Filtrar la lista de plantillas para excluir las que ya están en la rutina
            List<Serie> seriesDisponibles = todasLasSeriesPlantilla.stream()
                    .filter(plantilla -> !idsSeriesEnRutina.contains(plantilla.getId()))
                    .collect(Collectors.toList());

            model.addAttribute("rutina", rutina);
            model.addAttribute("seriesDisponibles", seriesDisponibles);
            model.addAttribute("usuario", usuarioActual);

            return "rutinas/editarRutina";
        } catch (ResourceNotFoundException e) {
            return "redirect:/profesor/dashboard?error=Rutina no encontrada";
        }
    }

    // POST: Actualizar rutina
    @PostMapping("/actualizar/{id}")
    public String actualizarRutina(@PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam String categoria,
            @RequestParam(required = false) List<Long> seriesIds,
            @RequestParam(required = false) List<Integer> repeticionesExistentes,
            @RequestParam(required = false) List<Long> nuevasSeriesIds,
            @RequestParam(required = false) List<Integer> repeticionesNuevas,
            Model model) {
        try {
            // Actualiza la información básica de la rutina
            rutinaService.actualizarInformacionBasicaRutina(id, nombre, descripcion, categoria);

            // Lógica para actualizar las series de la rutina
            rutinaService.actualizarSeriesDeRutina(id, seriesIds, repeticionesExistentes, nuevasSeriesIds,
                    repeticionesNuevas);

            return "redirect:/profesor/dashboard?success=Rutina actualizada exitosamente";
        } catch (Exception e) {
            return "redirect:/rutinas/editar/" + id + "?error=" + e.getMessage();
        }
    }

    // GET: Eliminar rutina
    @GetMapping("/eliminar/{id}")
    public String eliminarRutina(@PathVariable Long id) {
        try {
            rutinaService.eliminarRutina(id);

            return "redirect:/profesor/dashboard?success=Rutina eliminada exitosamente";
        } catch (ResourceNotFoundException e) {
            return "redirect:/profesor/dashboard?error=Rutina no encontrada";
        }
    }

    // Cambiar estado de rutina (En proceso/Terminada)
    @PostMapping("/cambiar-estado")
    public String cambiarEstadoRutina(@RequestParam Long rutinaId, @RequestParam Long alumnoId) {
        Rutina rutina = rutinaService.obtenerRutinaPorId(rutinaId);
        if (rutina != null) {
            if ("TERMINADA".equals(rutina.getEstado())) {
                rutinaService.cambiarEstadoRutina(rutinaId, "EN_PROCESO");
            } else {
                rutinaService.cambiarEstadoRutina(rutinaId, "TERMINADA");
            }
        }
        return "redirect:/profesor/alumnos/" + alumnoId;
    }

    // HOJA DE RUTINA VISUAL (link público con token)
    @GetMapping("/hoja/{tokenPublico}")
    public String verHojaRutina(@PathVariable String tokenPublico, Model model) {
        Rutina rutina = rutinaService.obtenerRutinaPorToken(tokenPublico);
        if (rutina == null) {
            return "redirect:/profesor/dashboard?error=Rutina no encontrada";
        }
        // Adaptar la estructura para la vista: cada serie tendrá una lista de
        // ejercicios enriquecidos
        // con los datos necesarios (nombre, descripcion, imagen, repeticiones, etc)
        // Sin modificar entidades ni la base de datos
        model.addAttribute("rutina", rutina);
        return "rutinas/verRutina";
    }

    // Redirección para evitar pantalla verde si acceden por /rutinas/ver/{rutinaId}
    @GetMapping("/ver/{rutinaId}")
    public String redirigirVerHojaRutina(@PathVariable Long rutinaId) {
        Rutina rutina = rutinaService.obtenerRutinaPorId(rutinaId);
        return "redirect:/rutinas/hoja/" + rutina.getTokenPublico();
    }
}
