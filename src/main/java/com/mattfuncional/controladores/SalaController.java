package com.mattfuncional.controladores;

import com.mattfuncional.dto.PizarraEstadoDTO;
import com.mattfuncional.servicios.PizarraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sala")
public class SalaController {

    @Autowired
    private PizarraService pizarraService;

    /**
     * Vista HTML para TV (fullscreen con F11).
     */
    @GetMapping("/{token}")
    public String verSala(@PathVariable String token, Model model) {
        try {
            PizarraEstadoDTO estado = pizarraService.construirEstadoParaSala(token);
            model.addAttribute("estado", estado);
            model.addAttribute("token", token);
            return "sala/sala";
        } catch (Exception e) {
            model.addAttribute("error", "Pizarra no encontrada");
            return "sala/sala-error";
        }
    }

    /**
     * API JSON para polling desde la vista TV.
     */
    @GetMapping(value = "/api/{token}/estado", produces = "application/json")
    @ResponseBody
    public ResponseEntity<PizarraEstadoDTO> getEstado(@PathVariable String token) {
        try {
            PizarraEstadoDTO estado = pizarraService.construirEstadoParaSala(token);
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
