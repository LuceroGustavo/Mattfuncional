package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    Profesor findByCorreo(String correo); // para validaciones o login futuro

    // --- CONSULTAS OPTIMIZADAS PARA FASE 3 ---

    // Obtener profesores con usuarios cargados
    @Query("SELECT p FROM Profesor p LEFT JOIN FETCH p.usuarios")
    List<Profesor> findAllWithUsuarios();

    // Obtener profesor específico con todas las relaciones
    @Query("SELECT p FROM Profesor p LEFT JOIN FETCH p.usuarios WHERE p.id = :id")
    Profesor findByIdWithRelations(@Param("id") Long id);

    // Obtener profesores con conteo de alumnos
    @Query("SELECT p, COUNT(u) as alumnoCount FROM Profesor p LEFT JOIN p.usuarios u GROUP BY p")
    List<Object[]> findAllWithAlumnoCount();
}
