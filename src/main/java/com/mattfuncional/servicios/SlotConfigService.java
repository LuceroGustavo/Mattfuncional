package com.mattfuncional.servicios;

import com.mattfuncional.entidades.SlotConfig;
import com.mattfuncional.enums.DiaSemana;
import com.mattfuncional.repositorios.SlotConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;

@Service
public class SlotConfigService {
    @Autowired
    private SlotConfigRepository slotConfigRepository;

    public int getCapacidadMaxima(DiaSemana dia, LocalTime horaInicio, int defaultValue) {
        Optional<SlotConfig> config = slotConfigRepository.findByDiaAndHoraInicio(dia, horaInicio);
        return config.map(SlotConfig::getCapacidadMaxima).orElse(defaultValue);
    }

    public void setCapacidadMaxima(DiaSemana dia, LocalTime horaInicio, int capacidadMaxima) {
        SlotConfig config = slotConfigRepository.findByDiaAndHoraInicio(dia, horaInicio)
                .orElseGet(() -> {
                    SlotConfig nuevo = new SlotConfig();
                    nuevo.setDia(dia);
                    nuevo.setHoraInicio(horaInicio);
                    return nuevo;
                });
        config.setCapacidadMaxima(capacidadMaxima);
        slotConfigRepository.save(config);
    }
}