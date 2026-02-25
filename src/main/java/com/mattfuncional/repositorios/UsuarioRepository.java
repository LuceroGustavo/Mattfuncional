package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.entidades.Profesor;
import com.mattfuncional.enums.TipoAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByNombre(String nombre);

    Optional<Usuario> findByCorreo(String correo);

    /** Evita "Query did not return a unique result" cuando hay varios usuarios con el mismo correo. */
    Optional<Usuario> findFirstByCorreo(String correo);

    /**
     * Usuarios que pueden iniciar sesión (ADMIN, AYUDANTE, DEVELOPER).
     * Excluye ALUMNO. Usa "First" para evitar error si hay duplicados por correo.
     */
    Optional<Usuario> findFirstByCorreoAndRolIn(String correo, List<String> roles);

    java.util.List<Usuario> findAllByProfesorId(Long profesorId);

    Usuario findByProfesor(Profesor profesor);

    List<Usuario> findByRol(String rol);

    @Query("SELECT u FROM Usuario u WHERE u.rol IN :roles ORDER BY u.nombre")
    List<Usuario> findByRolIn(@Param("roles") List<String> roles);

    List<Usuario> findByProfesor_Id(Long profesorId);

    List<Usuario> findByProfesor_IdAndRol(Long profesorId, String rol);

    List<Usuario> findByTipoAsistencia(TipoAsistencia tipoAsistencia);

    // --- CONSULTAS OPTIMIZADAS PARA FASE 3 ---

    // Obtener usuarios con profesor cargado en una sola consulta
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.profesor WHERE u.rol = 'ALUMNO'")
    List<Usuario> findAllAlumnosWithProfesor();

    // Obtener todos los alumnos incluyendo los que no tienen profesor asignado
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.profesor WHERE u.rol = 'ALUMNO' ORDER BY u.nombre")
    List<Usuario> findAllAlumnosIncludingOrphans();

    // Obtener usuarios por profesor con todas las relaciones cargadas
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.profesor LEFT JOIN FETCH u.rutinas WHERE u.profesor.id = :profesorId AND u.rol = 'ALUMNO'")
    List<Usuario> findAlumnosByProfesorIdWithRelations(@Param("profesorId") Long profesorId);

    // Obtener usuario específico con todas las relaciones para dashboard
    // NOTA: No se pueden hacer JOIN FETCH de múltiples colecciones @OneToMany en una sola consulta
    // Por eso cargamos solo rutinas aquí, y medicionesFisicas se cargarán en una consulta separada si es necesario
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.profesor LEFT JOIN FETCH u.rutinas WHERE u.id = :id")
    Optional<Usuario> findByIdWithAllRelations(@Param("id") Long id);
    
    // Consulta separada para cargar mediciones físicas si es necesario
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.medicionesFisicas WHERE u.id = :id")
    Optional<Usuario> findByIdWithMediciones(@Param("id") Long id);

    // Obtener usuarios con conteo de rutinas (optimizado)
    @Query("SELECT u, COUNT(r) as rutinaCount FROM Usuario u LEFT JOIN u.rutinas r WHERE u.rol = 'ALUMNO' GROUP BY u")
    List<Object[]> findAlumnosWithRutinaCount();

    // Método para verificar si hay usuarios sin avatar asignado
    long countByAvatarIsNullOrAvatar(String avatar);

    /** Detecta correos duplicados (varios usuarios con el mismo correo). Para avisos de calidad de datos. */
    @Query(value = "SELECT correo, COUNT(*) AS cnt FROM usuario GROUP BY correo HAVING COUNT(*) > 1", nativeQuery = true)
    List<Object[]> findCorreosDuplicados();
}