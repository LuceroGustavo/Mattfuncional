package com.mattfuncional.servicios;

import com.mattfuncional.entidades.Categoria;
import com.mattfuncional.entidades.Profesor;
import com.mattfuncional.repositorios.CategoriaRepository;
import com.mattfuncional.repositorios.RutinaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CategoriaService {

    private static final String[] NOMBRES_CATEGORIAS_SISTEMA = {
            "FUERZA", "CARDIO", "FLEXIBILIDAD", "FUNCIONAL", "HIIT"
    };

    private final CategoriaRepository categoriaRepository;
    private final RutinaRepository rutinaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, RutinaRepository rutinaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.rutinaRepository = rutinaRepository;
    }

    public List<Categoria> findDisponiblesParaProfesor(Long profesorId) {
        return categoriaRepository.findDisponiblesParaProfesor(profesorId);
    }

    public List<Categoria> findByProfesorId(Long profesorId) {
        return categoriaRepository.findByProfesorIdOrderByNombreAsc(profesorId);
    }

    public List<Categoria> findCategoriasSistema() {
        return categoriaRepository.findByProfesorIsNullOrderByNombreAsc();
    }

    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id);
    }

    public Optional<Categoria> findByNombreSistema(String nombre) {
        return categoriaRepository.findFirstByNombreAndProfesorIsNull(nombre);
    }

    @Transactional(readOnly = true)
    public Set<Categoria> resolveCategoriasByNames(List<String> names, Long profesorId) {
        if (names == null || names.isEmpty()) return new HashSet<>();
        Set<Categoria> result = new HashSet<>();
        for (String nombre : names) {
            if (nombre == null || nombre.isBlank()) continue;
            String n = nombre.trim().toUpperCase();
            Optional<Categoria> cat = categoriaRepository.findFirstByNombreAndProfesorIsNull(n);
            if (cat.isEmpty() && profesorId != null) {
                cat = categoriaRepository.findFirstByNombreAndProfesorId(n, profesorId);
            }
            cat.ifPresent(result::add);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Set<Categoria> resolveCategoriasByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        Set<Categoria> result = new HashSet<>();
        for (Long id : ids) {
            if (id != null) {
                categoriaRepository.findById(id).ifPresent(result::add);
            }
        }
        return result;
    }

    @Transactional
    public void asegurarCategoriasSistema() {
        long presentes = categoriaRepository.countDistinctNombreSistemaPresentes(Arrays.asList(NOMBRES_CATEGORIAS_SISTEMA));
        if (presentes >= NOMBRES_CATEGORIAS_SISTEMA.length) {
            return;
        }
        for (String nombre : NOMBRES_CATEGORIAS_SISTEMA) {
            if (categoriaRepository.findFirstByNombreAndProfesorIsNull(nombre).isEmpty()) {
                categoriaRepository.save(new Categoria(nombre, null));
            }
        }
    }

    @Transactional
    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }

    public boolean existeNombreParaProfesor(String nombre, Long profesorId) {
        if (profesorId == null) {
            return categoriaRepository.findFirstByNombreAndProfesorIsNull(nombre).isPresent();
        }
        return categoriaRepository.findFirstByNombreAndProfesorId(nombre, profesorId).isPresent();
    }

    public boolean puedeSerEditadoPorProfesor(Long categoriaId, Long profesorId) {
        if (categoriaId == null || profesorId == null) return false;
        return categoriaRepository.findById(categoriaId)
                .filter(c -> c.getProfesor() != null && c.getProfesor().getId().equals(profesorId))
                .isPresent();
    }

    public boolean hayRutinasEnUso(Long categoriaId) {
        return !rutinaRepository.findByCategoriaId(categoriaId).isEmpty();
    }

    @Transactional
    public void ensureCategoriaExiste(String nombre, boolean esSistema, Profesor profesor) {
        if (nombre == null || nombre.isBlank()) return;
        String n = nombre.trim().toUpperCase();
        if (esSistema) {
            if (categoriaRepository.findFirstByNombreAndProfesorIsNull(n).isEmpty()) {
                categoriaRepository.save(new Categoria(n, null));
            }
        } else if (profesor != null) {
            if (categoriaRepository.findFirstByNombreAndProfesorId(n, profesor.getId()).isEmpty()) {
                categoriaRepository.save(new Categoria(n, profesor));
            }
        }
    }

    @Transactional
    public void eliminarCategoriasDelProfesor(Long profesorId) {
        if (profesorId == null) return;
        categoriaRepository.deleteByProfesor_Id(profesorId);
    }
}
