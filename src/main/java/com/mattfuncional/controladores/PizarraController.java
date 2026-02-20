package com.mattfuncional.controladores;

import com.mattfuncional.entidades.Exercise;
import com.mattfuncional.entidades.GrupoMuscular;
import com.mattfuncional.entidades.Pizarra;
import com.mattfuncional.entidades.Profesor;
import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.servicios.ExerciseService;
import com.mattfuncional.servicios.GrupoMuscularService;
import com.mattfuncional.servicios.PizarraService;
import com.mattfuncional.servicios.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/profesor/pizarra")
public class PizarraController {

    @Autowired
    private PizarraService pizarraService;

    @Autowired
    private ProfesorService profesorService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private GrupoMuscularService grupoMuscularService;

    private Profesor getProfesorAcceso(Usuario usuario) {
        if (usuario == null) return null;
        if ("DEVELOPER".equals(usuario.getRol())) {
            return profesorService.getProfesorByCorreo("profesor@mattfuncional.com");
        }
        if (usuario.getProfesor() != null) return usuario.getProfesor();
        return profesorService.getProfesorByCorreo(usuario.getCorreo());
    }

    @GetMapping
    public String listar(Model model, @AuthenticationPrincipal Usuario usuario) {
        Profesor profesor = getProfesorAcceso(usuario);
        if (profesor == null) return "redirect:/login";
        List<Pizarra> pizarras = pizarraService.listarPorProfesor(profesor.getId());
        model.addAttribute("pizarras", pizarras);
        return "profesor/pizarra-lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model, @AuthenticationPrincipal Usuario usuario) {
        Profesor profesor = getProfesorAcceso(usuario);
        if (profesor == null) return "redirect:/login";
        model.addAttribute("cantidadColumnas", 1);
        return "profesor/pizarra-nueva";
    }

    @PostMapping("/crear")
    public String crear(@RequestParam String nombre,
                       @RequestParam(defaultValue = "1") int cantidadColumnas,
                       @AuthenticationPrincipal Usuario usuario) {
        Profesor profesor = getProfesorAcceso(usuario);
        if (profesor == null) return "redirect:/login";
        Pizarra p = pizarraService.crear(profesor, nombre, cantidadColumnas);
        return "redirect:/profesor/pizarra/editar/" + p.getId();
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model,
                        @AuthenticationPrincipal Usuario usuario,
                        @RequestParam(name = "grupoId", required = false) Long grupoId,
                        @RequestParam(name = "search", required = false) String search) {
        Profesor profesor = getProfesorAcceso(usuario);
        if (profesor == null) return "redirect:/login";
        Pizarra p = pizarraService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Pizarra no encontrada"));
        if (!p.getProfesor().getId().equals(profesor.getId())) {
            return "redirect:/profesor/pizarra?error=permiso";
        }
        List<Exercise> ejercicios = exerciseService.findEjerciciosDisponiblesParaProfesorWithImages(profesor.getId());
        if (grupoId != null) {
            ejercicios = ejercicios.stream()
                    .filter(e -> e.getGrupos() != null && e.getGrupos().stream().anyMatch(g -> grupoId.equals(g.getId())))
                    .toList();
        }
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            ejercicios = ejercicios.stream()
                    .filter(e -> e.getName().toLowerCase().contains(searchLower) ||
                            (e.getDescription() != null && e.getDescription().toLowerCase().contains(searchLower)))
                    .toList();
        }
        List<GrupoMuscular> gruposMusculares = grupoMuscularService.findDisponiblesParaProfesor(profesor.getId());
        model.addAttribute("pizarra", p);
        model.addAttribute("ejercicios", ejercicios);
        model.addAttribute("gruposMusculares", gruposMusculares);
        model.addAttribute("selectedGrupoId", grupoId);
        return "profesor/pizarra-editor";
    }

    @PostMapping("/actualizar-basico")
    @ResponseBody
    public ResponseEntity<?> actualizarBasico(@RequestBody Map<String, Object> body,
                                             @AuthenticationPrincipal Usuario usuario) {
        Profesor profesor = getProfesorAcceso(usuario);
        if (profesor == null) return ResponseEntity.status(401).build();
        Long id = Long.valueOf(body.get("id").toString());
        String nombre = (String) body.get("nombre");
        @SuppressWarnings("unchecked")
        List<String> titulos = (List<String>) body.get("titulos");
        pizarraService.actualizarBasico(id, nombre, titulos, profesor.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/agregar-item")
    @ResponseBody
    public ResponseEntity<?> agregarItem(@RequestBody Map<String, Object> body,
                                         @AuthenticationPrincipal Usuario usuario) {
        Profesor profesor = getProfesorAcceso(usuario);
        if (profesor == null) return ResponseEntity.status(401).build();
        Long columnaId = Long.valueOf(body.get("columnaId").toString());
        Long exerciseId = Long.valueOf(body.get("exerciseId").toString());
        Integer peso = body.get("peso") != null ? Integer.valueOf(body.get("peso").toString()) : null;
        Integer reps = body.get("repeticiones") != null ? Integer.valueOf(body.get("repeticiones").toString()) : null;
        String unidad = (String) body.get("unidad");
        if (unidad == null) unidad = "reps";
        var item = pizarraService.agregarItem(columnaId, exerciseId, peso, reps, unidad, profesor.getId());
        return ResponseEntity.ok(Map.of("id", item.getId()));
    }

    @PostMapping("/actualizar-item")
    @ResponseBody
    public ResponseEntity<?> actualizarItem(@RequestBody Map<String, Object> body,
                                            @AuthenticationPrincipal Usuario usuario) {
        Profesor profesor = getProfesorAcceso(usuario);
        if (profesor == null) return ResponseEntity.status(401).build();
        Long itemId = Long.valueOf(body.get("itemId").toString());
        Integer peso = body.get("peso") != null ? Integer.valueOf(body.get("peso").toString()) : null;
        Integer reps = body.get("repeticiones") != null ? Integer.valueOf(body.get("repeticiones").toString()) : null;
        String unidad = (String) body.get("unidad");
        pizarraService.actualizarItem(itemId, peso, reps, unidad, profesor.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/eliminar-item")
    @ResponseBody
    public ResponseEntity<?> eliminarItem(@RequestBody Map<String, Object> body,
                                          @AuthenticationPrincipal Usuario usuario) {
        Profesor profesor = getProfesorAcceso(usuario);
        if (profesor == null) return ResponseEntity.status(401).build();
        Long itemId = Long.valueOf(body.get("itemId").toString());
        pizarraService.eliminarItem(itemId, profesor.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        Profesor profesor = getProfesorAcceso(usuario);
        if (profesor == null) return "redirect:/login";
        pizarraService.eliminar(id, profesor.getId());
        return "redirect:/profesor/pizarra";
    }
}
