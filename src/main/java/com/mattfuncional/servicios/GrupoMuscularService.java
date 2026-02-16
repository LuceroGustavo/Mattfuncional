package com.mattfuncional.servicios;

import com.mattfuncional.entidades.GrupoMuscular;
import com.mattfuncional.entidades.Profesor;
import com.mattfuncional.repositorios.GrupoMuscularRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GrupoMuscularService {

    private static final String[] NOMBRES_GRUPOS_SISTEMA = {
        "BRAZOS", "PIERNAS", "PECHO", "ESPALDA", "CARDIO", "ELONGACION"
    };

    private final GrupoMuscularRepository grupoMuscularRepository;

    public GrupoMuscularService(GrupoMuscularRepository grupoMuscularRepository) {
        this.grupoMuscularRepository = grupoMuscularRepository;
    }

    /** Grupos disponibles para un profesor: del sistema + los suyos. */
    public List<GrupoMuscular> findDisponiblesParaProfesor(Long profesorId) {
        return grupoMuscularRepository.findDisponiblesParaProfesor(profesorId);
    }

    public List<GrupoMuscular> findByProfesorId(Long profesorId) {
        return grupoMuscularRepository.findByProfesorIdOrderByNombreAsc(profesorId);
    }

    public List<GrupoMuscular> findGruposSistema() {
        return grupoMuscularRepository.findByProfesorIsNullOrderByNombreAsc();
    }

    public Optional<GrupoMuscular> findById(Long id) {
        return grupoMuscularRepository.findById(id);
    }

    public Optional<GrupoMuscular> findByNombreSistema(String nombre) {
        return grupoMuscularRepository.findByNombreAndProfesorIsNull(nombre);
    }

    /** Convierte una lista de IDs a Set de GrupoMuscular (ignora IDs no encontrados). */
    @Transactional(readOnly = true)
    public Set<GrupoMuscular> resolveGruposByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        Set<GrupoMuscular> result = new HashSet<>();
        for (Long id : ids) {
            if (id != null) {
                grupoMuscularRepository.findById(id).ifPresent(result::add);
            }
        }
        return result;
    }

    /** Resuelve nombres a Set de GrupoMuscular: primero sistema, luego del profesor si se indica. */
    @Transactional(readOnly = true)
    public Set<GrupoMuscular> resolveGruposByNames(List<String> names, Long profesorId) {
        if (names == null || names.isEmpty()) return new HashSet<>();
        Set<GrupoMuscular> result = new HashSet<>();
        for (String nombre : names) {
            if (nombre == null || nombre.isBlank()) continue;
            String n = nombre.trim();
            Optional<GrupoMuscular> gSistema = grupoMuscularRepository.findByNombreAndProfesorIsNull(n);
            if (gSistema.isPresent()) {
                result.add(gSistema.get());
            } else if (profesorId != null) {
                grupoMuscularRepository.findByNombreAndProfesorId(n, profesorId).ifPresent(result::add);
            }
        }
        return result;
    }

    /** Asegura que existan los 6 grupos del sistema. Idempotente. */
    @Transactional
    public void asegurarGruposSistema() {
        for (String nombre : NOMBRES_GRUPOS_SISTEMA) {
            if (grupoMuscularRepository.findByNombreAndProfesorIsNull(nombre).isEmpty()) {
                GrupoMuscular g = new GrupoMuscular(nombre, null);
                grupoMuscularRepository.save(g);
            }
        }
    }

    @Transactional
    public GrupoMuscular guardar(GrupoMuscular grupo) {
        return grupoMuscularRepository.save(grupo);
    }

    @Transactional
    public void eliminar(Long id) {
        grupoMuscularRepository.deleteById(id);
    }

    public boolean existeNombreParaProfesor(String nombre, Long profesorId) {
        if (profesorId == null) {
            return grupoMuscularRepository.findByNombreAndProfesorIsNull(nombre).isPresent();
        }
        return grupoMuscularRepository.findByNombreAndProfesorId(nombre, profesorId).isPresent();
    }

    /** Solo los grupos con profesor no nulo (creados por el profesor) pueden editarse/eliminarse por ese profesor. */
    public boolean puedeSerEditadoPorProfesor(Long grupoId, Long profesorId) {
        if (grupoId == null || profesorId == null) return false;
        return grupoMuscularRepository.findById(grupoId)
                .filter(g -> g.getProfesor() != null && g.getProfesor().getId().equals(profesorId))
                .isPresent();
    }
}
