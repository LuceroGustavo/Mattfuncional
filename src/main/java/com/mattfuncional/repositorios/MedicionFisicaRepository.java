package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.MedicionFisica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface MedicionFisicaRepository extends JpaRepository<MedicionFisica, Long> {
    List<MedicionFisica> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
} 