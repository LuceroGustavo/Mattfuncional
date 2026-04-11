package com.mattfuncional.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Raíz de almacenamiento de archivos subidos. Si la propiedad es relativa (p. ej. "uploads"),
 * se usa una carpeta estable bajo el directorio del usuario para evitar rutas bajo el tmp de Tomcat
 * (donde falla transferTo o se pierde al reiniciar).
 */
@Component
public class MattUploadsPathResolver {

    private static final Logger log = LoggerFactory.getLogger(MattUploadsPathResolver.class);

    private final Path rootAbsolute;

    public MattUploadsPathResolver(
            @Value("${mattfuncional.uploads.dir:uploads}") String configuredDir) {
        Path p = Paths.get(configuredDir.trim());
        if (!p.isAbsolute()) {
            p = Paths.get(System.getProperty("user.home"), "Mattfuncional", "uploads");
        }
        this.rootAbsolute = p.toAbsolutePath().normalize();
    }

    @PostConstruct
    void logRuta() {
        log.info("Almacenamiento de subidas (Mattfuncional): {} — promociones: {}",
                rootAbsolute, rootAbsolute.resolve("promociones-publicas"));
    }

    public Path getRoot() {
        return rootAbsolute;
    }
}
