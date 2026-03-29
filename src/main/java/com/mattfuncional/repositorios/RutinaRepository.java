package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    /** Carga la rutina con series y categorías para edición y copias. */
    @Query("SELECT DISTINCT r FROM Rutina r LEFT JOIN FETCH r.series LEFT JOIN FETCH r.categorias WHERE r.id = :id")
    Optional<Rutina> findByIdWithSeries(@Param("id") Long id);

    List<Rutina> findByUsuarioId(Long usuarioId); // ← ESTA ES LA CLAVE

    List<Rutina> findByProfesorId(Long profesorId);

    List<Rutina> findByUsuarioIdAndEstado(Long usuarioId, String estado);

    List<Rutina> findByProfesorIdAndEstado(Long profesorId, String estado);

    List<Rutina> findByEstado(String estado);

    // Buscar rutinas plantilla por profesor
    List<Rutina> findByProfesorIdAndEsPlantillaTrue(Long profesorId);

    @Query("SELECT DISTINCT r FROM Rutina r JOIN r.categorias c WHERE r.profesor.id = :profesorId AND r.esPlantilla = true AND LOWER(c.nombre) LIKE LOWER(CONCAT('%', :categoria, '%'))")
    List<Rutina> findByProfesorIdAndEsPlantillaTrueAndCategoriaContaining(@Param("profesorId") Long profesorId, @Param("categoria") String categoria);

    @Query("SELECT r FROM Rutina r JOIN r.categorias c WHERE c.id = :categoriaId")
    List<Rutina> findByCategoriaId(@Param("categoriaId") Long categoriaId);

    // Buscar rutinas plantilla por profesor y nombre
    List<Rutina> findByProfesorIdAndEsPlantillaTrueAndNombreContainingIgnoreCase(Long profesorId, String nombre);

    Optional<Rutina> findByNombreAndEsPlantillaTrueAndProfesorId(String nombre, Long profesorId);

    /** Evita "Query did not return unique result" si hay rutinas duplicadas. */
    Optional<Rutina> findFirstByNombreAndEsPlantillaTrueAndProfesorId(String nombre, Long profesorId);

    // Buscar todas las rutinas plantilla
    List<Rutina> findByEsPlantillaTrue();

    // Buscar rutinas plantilla por creador
    List<Rutina> findByCreadorAndEsPlantillaTrue(String creador);

    // Buscar rutinas asignadas a usuarios por profesor
    List<Rutina> findByProfesorIdAndEsPlantillaFalse(Long profesorId);

    // Buscar rutinas asignadas a un usuario específico (no plantillas)
    List<Rutina> findByUsuarioIdAndEsPlantillaFalse(Long usuarioId);

    Optional<Rutina> findByTokenPublico(String tokenPublico);

    /** Carga la rutina por token con sus series (sin serieEjercicios, para combinar con carga por serie). */
    @Query("SELECT DISTINCT r FROM Rutina r LEFT JOIN FETCH r.series WHERE r.tokenPublico = :token")
    Optional<Rutina> findByTokenPublicoWithSeries(@Param("token") String tokenPublico);

    boolean existsByTokenPublico(String tokenPublico);

    /** Rutinas asignadas (no plantillas) creadas antes de la fecha indicada (para depuración). */
    List<Rutina> findByEsPlantillaFalseAndFechaCreacionBefore(LocalDateTime fecha);
}
