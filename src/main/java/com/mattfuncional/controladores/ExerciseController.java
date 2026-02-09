package com.mattfuncional.controladores;

import com.mattfuncional.entidades.Exercise;
import com.mattfuncional.entidades.Imagen;
import com.mattfuncional.enums.MuscleGroup;
import com.mattfuncional.servicios.ExerciseService;
import com.mattfuncional.servicios.ImagenServicio;
import com.mattfuncional.servicios.ProfesorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private ImagenServicio imagenServicio;

    @Autowired
    private ProfesorService profesorService;

    // Método para listar todos los ejercicios
    @GetMapping("/exercise/lista")
    public String getExerciseList(@RequestParam(name = "muscleGroup", required = false) String muscleGroupStr,
            Model model, @AuthenticationPrincipal com.mattfuncional.entidades.Usuario usuarioActual) {
        List<Exercise> exercises;
        final MuscleGroup muscleGroup;
        if (muscleGroupStr != null) {
            MuscleGroup tempGroup = null;
            try {
                tempGroup = MuscleGroup.valueOf(muscleGroupStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                model.addAttribute("errorMessage", "Grupo muscular inválido.");
            }
            muscleGroup = tempGroup;
        } else {
            muscleGroup = null;
        }
        if (usuarioActual != null && "ADMIN".equals(usuarioActual.getRol()) && usuarioActual.getProfesor() != null) {
            // Profesor: ver ejercicios disponibles (predeterminados + propios)
            Long profesorId = usuarioActual.getProfesor().getId();
            if (muscleGroup != null) {
                exercises = exerciseService.findEjerciciosDisponiblesParaProfesor(profesorId).stream()
                        .filter(e -> e.getMuscleGroups() != null && e.getMuscleGroups().contains(muscleGroup))
                        .toList();
            } else {
                exercises = exerciseService.findEjerciciosDisponiblesParaProfesor(profesorId);
            }
        } else {
            // Otro rol o no autenticado: solo predeterminados
            if (muscleGroup != null) {
                exercises = exerciseService.findEjerciciosPredeterminados().stream()
                        .filter(e -> e.getMuscleGroups() != null && e.getMuscleGroups().contains(muscleGroup))
                        .toList();
            } else {
                exercises = exerciseService.findEjerciciosPredeterminados();
            }
        }
        model.addAttribute("exercises", exercises);
        model.addAttribute("muscleGroups", MuscleGroup.values());
        model.addAttribute("selectedMuscleGroup", muscleGroup);
        return "ejercicios/exercise-lista";
    }

    // Método para mostrar el formulario de nuevo ejercicio
    @GetMapping("/ejercicios/nuevo")
    public String cargarFormularioEjercicio(Model model) {
        model.addAttribute("exercise", new Exercise());
        model.addAttribute("muscleGroups", MuscleGroup.values());
        return "ejercicios/formulario-ejercicio";
    }

    // Método para guardar un nuevo ejercicio
    @PostMapping("/ejercicios/nuevo")
    public String guardarEjercicio(@Valid @ModelAttribute("exercise") Exercise exercise,
            BindingResult bindingResult,
            @RequestParam List<String> muscleGroups,
            @RequestParam("image") MultipartFile imageFile,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("muscleGroups", MuscleGroup.values());
            return "ejercicios/formulario-ejercicio";
        }

        // Asignar los grupos musculares al ejercicio antes de guardar
        Set<MuscleGroup> muscleGroupSet = new HashSet<>();
        for (String muscleGroup : muscleGroups) {
            muscleGroupSet.add(MuscleGroup.valueOf(muscleGroup));
        }
        exercise.setMuscleGroups(muscleGroupSet);

        try {
            exerciseService.saveExercise(exercise, imageFile);
            return "redirect:/exercise/lista";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("muscleGroups", MuscleGroup.values());
            return "ejercicios/formulario-ejercicio";
        }
    }

    // Ruta para la administración de ejercicios (ABM)
    @GetMapping("/exercise/editar")
    public String listarEjercicios(Model model) {
        List<Exercise> exercises = exerciseService.findAllExercises();
        model.addAttribute("exercises", exercises);
        return "ejercicios/abm-ejercicios";
    }

    @GetMapping("/ejercicios/abm")
    public String abmEjercicios(Model model) {
        List<Exercise> exercises = exerciseService.findAllExercises();
        model.addAttribute("exercises", exercises);
        return "ejercicios/abm-ejercicios";
    }

    // Método para mostrar el formulario de modificación de ejercicios
    @GetMapping("/ejercicios/modificar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model,
            @AuthenticationPrincipal com.mattfuncional.entidades.Usuario usuarioActual) {
        Exercise exercise = exerciseService.findById(id);
        if (exercise == null) {
            return "redirect:/ejercicios/abm";
        }
        // Solo el admin o el profesor dueño puede editar
        if (usuarioActual != null && "ADMIN".equals(usuarioActual.getRol())) {
            if (exercise.getProfesor() == null
                    || !exercise.getProfesor().getId().equals(usuarioActual.getProfesor().getId())) {
                return "redirect:/exercise/lista?error=permiso";
            }
        }
        model.addAttribute("exercise", exercise);
        model.addAttribute("muscleGroups", MuscleGroup.values());
        return "ejercicios/formulario-modificar-ejercicio";
    }

    // Método para procesar la modificación de un ejercicio
    @PostMapping("/ejercicios/modificar/{id}")
    public String procesarFormularioEdicion(@PathVariable("id") Long id,
            @Valid @ModelAttribute("exercise") Exercise exercise,
            BindingResult bindingResult,
            @RequestParam List<String> muscleGroups,
            Model model,
            @AuthenticationPrincipal com.mattfuncional.entidades.Usuario usuarioActual) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("muscleGroups", MuscleGroup.values());
            return "ejercicios/formulario-modificar-ejercicio";
        }
        Exercise original = exerciseService.findById(id);
        // Solo el admin o el profesor dueño puede editar
        if (usuarioActual != null && "ADMIN".equals(usuarioActual.getRol())) {
            if (original.getProfesor() == null
                    || !original.getProfesor().getId().equals(usuarioActual.getProfesor().getId())) {
                return "redirect:/exercise/lista?error=permiso";
            }
        }
        Set<MuscleGroup> muscleGroupSet = new HashSet<>();
        for (String muscleGroup : muscleGroups) {
            muscleGroupSet.add(MuscleGroup.valueOf(muscleGroup));
        }
        exercise.setMuscleGroups(muscleGroupSet);
        try {
            exerciseService.modifyExercise(id, exercise, null, muscleGroupSet);
            return "redirect:/ejercicios/abm";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("muscleGroups", MuscleGroup.values());
            return "ejercicios/formulario-modificar-ejercicio";
        }
    }

    @GetMapping("/ejercicios/eliminar/{id}")
    public String eliminarEjercicio(@PathVariable("id") Long id,
            @AuthenticationPrincipal com.mattfuncional.entidades.Usuario usuarioActual) {
        Exercise exercise = exerciseService.findById(id);
        // Solo el admin o el profesor dueño puede eliminar
        if (usuarioActual != null && "ADMIN".equals(usuarioActual.getRol())) {
            if (exercise.getProfesor() == null
                    || !exercise.getProfesor().getId().equals(usuarioActual.getProfesor().getId())) {
                return "redirect:/exercise/lista?error=permiso";
            }
        }
        exerciseService.deleteExercise(id);
        return "redirect:/ejercicios/abm";
    }

    // Método para cambiar la imagen de un ejercicio
    @PostMapping("/ejercicios/cambiar-imagen/{id}")
    public String cambiarImagen(@PathVariable("id") Long id,
            @RequestParam("image") MultipartFile imageFile,
            Model model) {
        try {
            if (!imageFile.isEmpty()) {
                // Obtener el ejercicio actual
                Exercise exercise = exerciseService.findById(id);
                if (exercise == null) {
                    throw new RuntimeException("Ejercicio no encontrado con ID: " + id);
                }

                if (exercise.getImagen() != null) {
                    // Actualizar la imagen existente
                    Imagen nuevaImagen = imagenServicio.actualizar(imageFile, exercise.getImagen().getId());
                    exerciseService.updateImage(id, nuevaImagen);
                } else {
                    // Crear una nueva imagen y asociarla al ejercicio
                    Imagen nuevaImagen = imagenServicio.guardar(imageFile);
                    exerciseService.updateImage(id, nuevaImagen);
                }
            }
            return "redirect:/ejercicios/abm";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/ejercicios/abm";
        }
    }

    // ========== MÉTODOS ESPECÍFICOS PARA PROFESOR ==========

    // Lista de ejercicios del profesor - REDIRIGIDO A NUEVA VISTA OPTIMIZADA
    @GetMapping("/profesor/ejercicios")
    public String getProfesorExerciseList(@RequestParam(name = "muscleGroup", required = false) String muscleGroupStr,
            @RequestParam(name = "search", required = false) String search,
            @AuthenticationPrincipal com.mattfuncional.entidades.Usuario usuarioActual) {
        
        // Redirigir a la nueva vista optimizada con tabla
        return "redirect:/profesor/mis-ejercicios";
    }

    // Formulario para crear nuevo ejercicio (profesor) - REDIRIGIDO A NUEVA VISTA
    @GetMapping("/profesor/ejercicios/nuevo")
    public String cargarFormularioEjercicioProfesor(@AuthenticationPrincipal com.mattfuncional.entidades.Usuario usuarioActual) {
        
        // Redirigir a la nueva vista optimizada
        return "redirect:/profesor/mis-ejercicios/nuevo";
    }

    // Guardar nuevo ejercicio (profesor)
    @PostMapping("/profesor/ejercicios/nuevo")
    public String guardarEjercicioProfesor(@Valid @ModelAttribute("exercise") Exercise exercise,
            BindingResult bindingResult,
            @RequestParam List<String> muscleGroups,
            @RequestParam("image") MultipartFile imageFile,
            Model model,
            @AuthenticationPrincipal com.mattfuncional.entidades.Usuario usuarioActual) {

        if (usuarioActual == null || !"ADMIN".equals(usuarioActual.getRol())) {
            return "redirect:/login";
        }

        // Buscar el profesor por correo si no está asociado directamente
        com.mattfuncional.entidades.Profesor profesor = usuarioActual.getProfesor();
        if (profesor == null) {
            try {
                profesor = profesorService.getProfesorByCorreo(usuarioActual.getCorreo());
                if (profesor == null) {
                    return "redirect:/login?error=profesor_no_encontrado";
                }
            } catch (Exception e) {
                return "redirect:/login?error=error_buscar_profesor";
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("muscleGroups", MuscleGroup.values());
            model.addAttribute("profesor", profesor);
            return "ejercicios/formulario-ejercicio-profesor";
        }

        // Asignar el profesor automáticamente
        exercise.setProfesor(profesor);

        // Asignar los grupos musculares al ejercicio antes de guardar
        Set<MuscleGroup> muscleGroupSet = new HashSet<>();
        for (String muscleGroup : muscleGroups) {
            muscleGroupSet.add(MuscleGroup.valueOf(muscleGroup));
        }
        exercise.setMuscleGroups(muscleGroupSet);

        try {
            exerciseService.saveExercise(exercise, imageFile);
            return "redirect:/profesor/ejercicios";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("muscleGroups", MuscleGroup.values());
            model.addAttribute("profesor", profesor);
            return "ejercicios/formulario-ejercicio-profesor";
        }
    }

    // Formulario para editar ejercicio (profesor) - REDIRIGIDO A NUEVA VISTA
    @GetMapping("/profesor/ejercicios/editar/{id}")
    public String mostrarFormularioEdicionProfesor(@PathVariable("id") Long id,
            @AuthenticationPrincipal com.mattfuncional.entidades.Usuario usuarioActual) {
        
        // Redirigir a la nueva vista optimizada
        return "redirect:/profesor/mis-ejercicios/editar/" + id;
    }

    // Procesar edición de ejercicio (profesor)
    @PostMapping("/profesor/ejercicios/editar/{id}")
    public String procesarFormularioEdicionProfesor(@PathVariable("id") Long id,
            @Valid @ModelAttribute("exercise") Exercise exercise,
            BindingResult bindingResult,
            @RequestParam List<String> muscleGroups,
            @RequestParam("image") MultipartFile imageFile,
            Model model,
            @AuthenticationPrincipal com.mattfuncional.entidades.Usuario usuarioActual) {

        if (usuarioActual == null || !"ADMIN".equals(usuarioActual.getRol())) {
            return "redirect:/login";
        }

        // Buscar el profesor por correo si no está asociado directamente
        com.mattfuncional.entidades.Profesor profesor = usuarioActual.getProfesor();
        if (profesor == null) {
            try {
                profesor = profesorService.getProfesorByCorreo(usuarioActual.getCorreo());
                if (profesor == null) {
                    return "redirect:/login?error=profesor_no_encontrado";
                }
            } catch (Exception e) {
                return "redirect:/login?error=error_buscar_profesor";
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("muscleGroups", MuscleGroup.values());
            model.addAttribute("profesor", profesor);
            return "ejercicios/formulario-modificar-ejercicio-profesor";
        }

        Exercise original = exerciseService.findById(id);
        if (original == null) {
            return "redirect:/profesor/ejercicios";
        }

        // Verificar que el ejercicio pertenece al profesor
        if (!original.getProfesor().getId().equals(profesor.getId())) {
            return "redirect:/profesor/ejercicios?error=permiso";
        }

        // Mantener el profesor asignado
        exercise.setProfesor(profesor);

        Set<MuscleGroup> muscleGroupSet = new HashSet<>();
        for (String muscleGroup : muscleGroups) {
            muscleGroupSet.add(MuscleGroup.valueOf(muscleGroup));
        }
        exercise.setMuscleGroups(muscleGroupSet);

        try {
            exerciseService.modifyExercise(id, exercise, imageFile, muscleGroupSet);
            return "redirect:/profesor/ejercicios";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("muscleGroups", MuscleGroup.values());
            model.addAttribute("profesor", profesor);
            return "ejercicios/formulario-modificar-ejercicio-profesor";
        }
    }

    // Eliminar ejercicio (profesor) - REDIRIGIDO A NUEVA VISTA
    @GetMapping("/profesor/ejercicios/eliminar/{id}")
    public String eliminarEjercicioProfesor(@PathVariable("id") Long id,
            @AuthenticationPrincipal com.mattfuncional.entidades.Usuario usuarioActual) {
        
        // Redirigir a la nueva vista optimizada
        return "redirect:/profesor/mis-ejercicios";
    }
}