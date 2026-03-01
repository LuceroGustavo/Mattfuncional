package com.mattfuncional.controladores;

import com.mattfuncional.entidades.Usuario;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profesor")
public class AdminPanelController {

    @GetMapping("/administracion")
    public String panelAdministracion(@AuthenticationPrincipal Usuario usuarioActual) {
        if (usuarioActual == null || (!"ADMIN".equals(usuarioActual.getRol()) && !"DEVELOPER".equals(usuarioActual.getRol()))) {
            return "redirect:/profesor/dashboard";
        }
        return "profesor/administracion";
    }
}
