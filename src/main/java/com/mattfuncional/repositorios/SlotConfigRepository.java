package com.mattfuncional.repositorios;

import com.mattfuncional.entidades.SlotConfig;
import com.mattfuncional.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface SlotConfigRepository extends JpaRepository<SlotConfig, Long> {
    Optional<SlotConfig> findByDiaAndHoraInicio(DiaSemana dia, LocalTime horaInicio);
} 