package com.mattfuncional.controladores;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattfuncional.dto.SerieDTO;
import com.mattfuncional.entidades.Exercise;
import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.entidades.Serie;
import com.mattfuncional.servicios.ExerciseService;
import com.mattfuncional.servicios.SerieService;
import com.mattfuncional.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService serieService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private UsuarioService usuarioService;

    // GET: Mostrar el formulario para crear una nueva serie plantilla
    @GetMapping("/crear")
    public String mostrarFormularioCrearSerie(
            Model model,
            @AuthenticationPrincipal com.mattfuncional.entidades.Usuario usuarioActual,
            @RequestParam(name = "muscleGroup", required = false) String muscleGroupStr,
            @RequestParam(name = "search", required = false) String search) {
        if (usuarioActual == null || usuarioActual.getProfesor() == null) {
            // Si no es profesor, redirigir o mostrar error
            return "redirect:/login";
        }
        Long profesorId = usuarioActual.getProfesor().getId();
        // Obtener ejercicios disponibles: predeterminados + propios del profesor (con imágenes)
        List<Exercise> ejercicios = exerciseService.findEjerciciosDisponiblesParaProfesorWithImages(profesorId);

        // Filtrado por grupo muscular
        com.mattfuncional.enums.MuscleGroup tempMuscleGroup = null;
        if (muscleGroupStr != null && !muscleGroupStr.isEmpty()) {
            try {
                tempMuscleGroup = com.mattfuncional.enums.MuscleGroup.valueOf(muscleGroupStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                model.addAttribute("errorMessage", "Grupo muscular inválido.");
            }
        }
        final com.mattfuncional.enums.MuscleGroup muscleGroup = tempMuscleGroup;
        if (muscleGroup != null) {
            ejercicios = ejercicios.stream()
                    .filter(e -> e.getMuscleGroups() != null && e.getMuscleGroups().contains(muscleGroup))
                    .toList();
        }
        // Filtrado por nombre
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            ejercicios = ejercicios.stream()
                    .filter(e -> e.getName().toLowerCase().contains(searchLower) ||
                            (e.getDescription() != null && e.getDescription().toLowerCase().contains(searchLower)))
                    .toList();
        }
        model.addAttribute("ejercicios", ejercicios);
        model.addAttribute("serieDTO", new com.mattfuncional.dto.SerieDTO());
        model.addAttribute("usuario", usuarioActual);
        model.addAttribute("selectedMuscleGroup", muscleGroup);
        model.addAttribute("editMode", false);
        model.addAttribute("serieDTOJson", "null");
        return "series/crearSerie";
    }

    // POST: Recibe los datos del formulario y crea la serie plantilla
    @PostMapping("/crear-plantilla")
    @ResponseBody
    public ResponseEntity<?> crearSeriePlantilla(@RequestBody SerieDTO serieDTO,
            @AuthenticationPrincipal Usuario profesorUsuario) {
        try {
            // Obtenemos el usuario logueado (que es el profesor)
            if (profesorUsuario == null || profesorUsuario.getProfesor() == null) {
                return ResponseEntity.badRequest().body("Error: No se pudo identificar al profesor.");
            }

            // Asignamos el ID del profesor al DTO
            serieDTO.setProfesorId(profesorUsuario.getProfesor().getId());

            // Llamamos al servicio para crear la serie
            serieService.crearSeriePlantilla(serieDTO);

            // Devolvemos una respuesta exitosa
            return ResponseEntity.ok().body("Serie creada exitosamente");
        } catch (Exception e) {
            // En caso de error, devolvemos un mensaje
            return ResponseEntity.badRequest().body("Error al crear la serie: " + e.getMessage());
        }
    }

    // GET: Mostrar el formulario para editar una serie plantilla
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarSerie(@PathVariable Long id, Model model,
            @AuthenticationPrincipal Usuario profesorUsuario) {
        // 1. Obtener la serie CON sus ejercicios cargados (evita LazyInitialization y muestra la tabla)
        Serie serie = serieService.obtenerSeriePorIdConEjercicios(id);

        if (profesorUsuario.getProfesor() == null
                || !serie.getProfesor().getId().equals(profesorUsuario.getProfesor().getId())) {
            return "redirect:/profesor/dashboard?tab=series&error=permiso_serie";
        }

        // 2. Convertir la entidad a DTO para el formulario (ya con ejercicios cargados)
        SerieDTO serieDTO = serieService.convertirSerieADTO(serie);

        // 3. Preparar el modelo para la vista
        Long profesorId = profesorUsuario.getProfesor() != null ? profesorUsuario.getProfesor().getId() : null;
        List<Exercise> ejercicios;
        if (profesorId != null) {
            ejercicios = exerciseService.findEjerciciosDisponiblesParaProfesorWithImages(profesorId);
        } else {
            ejercicios = exerciseService.findAllExercisesWithImages();
        }
        model.addAttribute("ejercicios", ejercicios);
        model.addAttribute("serieDTO", serieDTO);
        model.addAttribute("editMode", true);
        model.addAttribute("usuario", profesorUsuario);

        // Pasar el DTO como JSON para que el JS reciba correctamente ejercicios (nombre, valor, unidad, peso)
        try {
            model.addAttribute("serieDTOJson", new ObjectMapper().writeValueAsString(serieDTO));
        } catch (JsonProcessingException e) {
            model.addAttribute("serieDTOJson", "null");
        }

        return "series/crearSerie"; // Reutilizamos la vista de creación
    }

    @GetMapping("/ver/{id}")
    public String verSerie(@PathVariable Long id, Model model,
            @AuthenticationPrincipal Usuario profesorUsuario) {
        Serie serie = serieService.obtenerSeriePorId(id);
        boolean esPropietario = profesorUsuario != null
                && profesorUsuario.getProfesor() != null
                && serie.getProfesor() != null
                && serie.getProfesor().getId().equals(profesorUsuario.getProfesor().getId());
        if (!esPropietario) {
            return "redirect:/profesor/dashboard?tab=series&error=permiso_serie";
        }
        model.addAttribute("serie", serie);
        return "series/verSerie";
    }

    // PUT: Recibe los datos del formulario y actualiza la serie plantilla
    @PutMapping("/editar/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarSeriePlantilla(@PathVariable Long id, @RequestBody SerieDTO serieDTO,
            @AuthenticationPrincipal Usuario profesorUsuario) {
        try {
            Serie serieExistente = serieService.obtenerSeriePorId(id);

            boolean esPropietario = profesorUsuario.getProfesor() != null && serieExistente.getProfesor() != null &&
                    serieExistente.getProfesor().getId().equals(profesorUsuario.getProfesor().getId());

            if (!esPropietario) {
                return ResponseEntity.status(403).body("No tiene permiso para editar esta serie.");
            }

            serieService.actualizarSeriePlantilla(id, serieDTO);
            return ResponseEntity.ok().body("Serie actualizada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar la serie: " + e.getMessage());
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarSerie(@PathVariable Long id, @AuthenticationPrincipal Usuario profesorUsuario) {
        Serie serie = serieService.obtenerSeriePorId(id);

        boolean esPropietario = profesorUsuario.getProfesor() != null && serie.getProfesor() != null &&
                serie.getProfesor().getId().equals(profesorUsuario.getProfesor().getId());

        if (esPropietario) {
            serieService.eliminarSerie(id);
            return "redirect:/profesor/" + profesorUsuario.getProfesor().getId() + "?tab=series";
        } else {
            return "redirect:/profesor/dashboard?tab=series&error=permiso_serie";
        }
    }
}