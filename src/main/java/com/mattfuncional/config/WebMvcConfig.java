package com.mattfuncional.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Asegura que GET /login muestre siempre la plantilla personalizada (login.html)
 * y no la página por defecto de Spring Security ("Please sign in").
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final MattUploadsPathResolver uploadsPathResolver;

    public WebMvcConfig(MattUploadsPathResolver uploadsPathResolver) {
        this.uploadsPathResolver = uploadsPathResolver;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    /** Imágenes de promociones en /planes (subidas desde administración). */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            var dir = uploadsPathResolver.getRoot().resolve("promociones-publicas");
            Files.createDirectories(dir);
            String location = dir.toUri().toString();
            if (!location.endsWith("/")) {
                location = location + "/";
            }
            registry.addResourceHandler("/media/promociones/**").addResourceLocations(location);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo preparar el directorio de promociones públicas", e);
        }
    }
}
