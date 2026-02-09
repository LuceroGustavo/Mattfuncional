package com.mattfuncional.config;

import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.servicios.ProfesorService;
import com.mattfuncional.entidades.Profesor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ProfesorService profesorService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            // El único rol que gestiona el panel es ADMIN (el profesor es el administrador)
            if (role.equals("ROLE_ADMIN")) {
                if (authentication.getPrincipal() instanceof Usuario) {
                    Usuario usuario = (Usuario) authentication.getPrincipal();
                    Profesor profesor = usuario.getProfesor() != null ? usuario.getProfesor() : profesorService.getProfesorByCorreo(usuario.getCorreo());
                    if (profesor != null) {
                        response.sendRedirect("/profesor/" + profesor.getId());
                        return;
                    }
                }
                response.sendRedirect("/profesor/dashboard");
                return;
            }
        }

        // Fallback por si no se encuentra un rol manejado
        response.sendRedirect("/");
    }
}