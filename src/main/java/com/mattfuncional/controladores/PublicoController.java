package com.mattfuncional.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/public")
public class PublicoController {

    /** Formulario de consulta desde la página de planes. Por ahora solo redirige con mensaje de éxito. */
    @PostMapping("/consulta")
    public String enviarConsulta(@RequestParam String nombre,
                                 @RequestParam String email,
                                 @RequestParam(required = false) String mensaje,
                                 RedirectAttributes redirectAttributes) {
        // TODO: guardar en BD o enviar mail
        redirectAttributes.addFlashAttribute("consultaEnviada", true);
        return "redirect:/planes?ok=consulta";
    }
}
