package com.mattfuncional.controladores;

import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.entidades.Exercise;
import com.mattfuncional.enums.MuscleGroup;
import com.mattfuncional.servicios.UsuarioService;
import com.mattfuncional.servicios.ExerciseService;
import com.mattfuncional.servicios.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/")
    public String index(Model model) {
        // Profesor único (gestor del panel) por correo
        com.mattfuncional.entidades.Profesor adminProfesor = profesorService.getProfesorByCorreo("profesor@mattfuncional.com");
        java.util.List<com.mattfuncional.entidades.Exercise> exercises;
        if (adminProfesor != null) {
            // Cargar solo 5 ejercicios destacados SIN imágenes para máximo rendimiento
            exercises = exerciseService.findExercisesByProfesorIdWithoutImages(adminProfesor.getId());
            // Tomar solo los primeros 5 ejercicios (más rápido que shuffle)
            java.util.List<com.mattfuncional.entidades.Exercise> ejerciciosDestacados = exercises.stream()
                    .limit(5)
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("ejerciciosAleatorios", ejerciciosDestacados);
        } else {
            exercises = java.util.Collections.emptyList();
            model.addAttribute("ejerciciosAleatorios", java.util.Collections.emptyList());
        }
        // NO agregar exercises al modelo para evitar cargar imágenes innecesarias
        
        // Agregar información del usuario actual si está autenticado
        try {
            com.mattfuncional.entidades.Usuario usuarioActual = usuarioService.getUsuarioActual();
            if (usuarioActual != null) {
                model.addAttribute("usuarioActual", usuarioActual);
            }
        } catch (Exception e) {
            // Usuario no autenticado, no hacer nada
        }
        
        return "index.html";
    }

    @GetMapping("/login")
    public String login() {
        return "login.html";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro.html";
    }

    @GetMapping("/demo")
    public String demo() {
        return "demo.html";
    }

    @GetMapping("/home")
    public String home() {
        return "home.html";
    }

    @GetMapping("/usuario")
    public String dashboardUsuario(Model model) {
        // Agregar usuario actual para el navbar
        model.addAttribute("usuario", usuarioService.getUsuarioActual());
        return "usuario/dashboard";
    }

    @GetMapping("/usuario/dashboard/{id}")
    public String redirigirDashboardSingular(@PathVariable Long id) {
        return "redirect:/usuarios/dashboard/" + id;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            com.mattfuncional.entidades.Usuario usuarioActual = usuarioService.getUsuarioActual();
            if (usuarioActual != null && "PROFESOR".equals(usuarioActual.getRol())) {
                com.mattfuncional.entidades.Profesor p = usuarioActual.getProfesor() != null ? usuarioActual.getProfesor() : profesorService.getProfesorByCorreo(usuarioActual.getCorreo());
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
