package com.mattfuncional.controladores;

import com.mattfuncional.entidades.Profesor;
import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.servicios.ProfesorService;
import com.mattfuncional.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/profesor/usuarios-sistema")
public class UsuariosSistemaController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProfesorService profesorService;

    @GetMapping
    public String verUsuariosSistema(@AuthenticationPrincipal Usuario usuarioActual, Model model) {
        if (usuarioActual == null || !"ADMIN".equals(usuarioActual.getRol())) {
            return "redirect:/profesor/dashboard";
        }
        model.addAttribute("usuariosSistema", usuarioService.getUsuariosSistema());
        model.addAttribute("usuarioActual", usuarioActual);
        return "profesor/usuarios-sistema";
    }

    @PostMapping("/crear")
    public String crearUsuarioSistema(@AuthenticationPrincipal Usuario usuarioActual,
                                      @RequestParam String nombre,
                                      @RequestParam String correo,
                                      @RequestParam String password,
                                      @RequestParam String rol,
                                      Model model) {
        if (usuarioActual == null || !"ADMIN".equals(usuarioActual.getRol())) {
            return "redirect:/profesor/dashboard";
        }
        try {
            Profesor profesor = usuarioActual.getProfesor() != null
                    ? usuarioActual.getProfesor()
                    : profesorService.getProfesorByCorreo(usuarioActual.getCorreo());
            usuarioService.crearUsuarioSistema(nombre, correo, password, rol, profesor);
            return "redirect:/profesor/usuarios-sistema?ok=creado";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("usuariosSistema", usuarioService.getUsuariosSistema());
            model.addAttribute("usuarioActual", usuarioActual);
            return "profesor/usuarios-sistema";
        }
    }

    @PostMapping("/rol")
    public String actualizarRol(@AuthenticationPrincipal Usuario usuarioActual,
                                @RequestParam Long usuarioId,
                                @RequestParam String rol) {
        if (usuarioActual == null || !"ADMIN".equals(usuarioActual.getRol())) {
            return "redirect:/profesor/dashboard";
        }
        if (usuarioActual.getId() != null && usuarioActual.getId().equals(usuarioId) && !"ADMIN".equalsIgnoreCase(rol)) {
            return "redirect:/profesor/usuarios-sistema?error=self-rol";
        }
        usuarioService.actualizarRolUsuario(usuarioId, rol);
        return "redirect:/profesor/usuarios-sistema?ok=rol";
    }

    @PostMapping("/password")
    public String actualizarPassword(@AuthenticationPrincipal Usuario usuarioActual,
                                     @RequestParam Long usuarioId,
                                     @RequestParam String password) {
        if (usuarioActual == null || !"ADMIN".equals(usuarioActual.getRol())) {
            return "redirect:/profesor/dashboard";
        }
        usuarioService.cambiarPasswordUsuario(usuarioId, password);
        return "redirect:/profesor/usuarios-sistema?ok=password";
    }
}
