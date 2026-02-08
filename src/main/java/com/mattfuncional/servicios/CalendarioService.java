package com.mattfuncional.servicios;

import com.mattfuncional.dto.CalendarioSemanalDTO;
import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.entidades.DiaHorarioAsistencia;
import com.mattfuncional.enums.DiaSemana;
import com.mattfuncional.repositorios.UsuarioRepository;
import com.mattfuncional.servicios.SlotConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalendarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SlotConfigService slotConfigService;

    private static final int CAPACIDAD_MAXIMA_POR_SLOT = 10; // Configurable
    private static final LocalTime HORA_INICIO = LocalTime.of(6, 0);
    private static final LocalTime HORA_FIN = LocalTime.of(20, 0);
    private static final int DURACION_SLOT_MINUTOS = 60;

    public CalendarioSemanalDTO generarCalendarioSemanal(LocalDate fecha, Long profesorId) {
        LocalDate inicioSemana = fecha.with(DayOfWeek.MONDAY);
        LocalDate finSemana = inicioSemana.plusDays(6);

        CalendarioSemanalDTO calendario = new CalendarioSemanalDTO(inicioSemana, finSemana);
        calendario.setCapacidadMaxima(CAPACIDAD_MAXIMA_POR_SLOT);

        // Generar slots para cada día
        Map<DiaSemana, List<CalendarioSemanalDTO.SlotHorarioDTO>> slotsPorDia = new HashMap<>();

        for (DiaSemana dia : DiaSemana.values()) {
            slotsPorDia.putIfAbsent(dia, new ArrayList<>());
        }

        for (DiaSemana dia : DiaSemana.values()) {
            List<CalendarioSemanalDTO.SlotHorarioDTO> slots = generarSlotsParaDia(dia);
            slotsPorDia.get(dia).addAll(slots);
        }

        calendario.setSlotsPorDia(slotsPorDia);

        // Cargar usuarios y asignarlos a los slots correspondientes
        List<Usuario> usuarios;
        if (profesorId != null) {
            usuarios = usuarioRepository.findByProfesor_IdAndRol(profesorId, "USER");
        } else {
            usuarios = usuarioRepository.findByTipoAsistencia(com.mattfuncional.enums.TipoAsistencia.PRESENCIAL);
        }
        asignarUsuariosASlots(calendario, usuarios);

        // Eliminar duplicados de usuarios en cada slot
        for (List<CalendarioSemanalDTO.SlotHorarioDTO> slots : calendario.getSlotsPorDia().values()) {
            for (CalendarioSemanalDTO.SlotHorarioDTO slot : slots) {
                if (slot.getUsuariosAsignados() != null && !slot.getUsuariosAsignados().isEmpty()) {
                    Set<Long> ids = new HashSet<>();
                    List<Usuario> unicos = new ArrayList<>();
                    for (Usuario u : slot.getUsuariosAsignados()) {
                        if (u != null && u.getId() != null && ids.add(u.getId())) {
                            unicos.add(u);
                        }
                    }
                    slot.setUsuariosAsignados(unicos);
                }
            }
        }

        return calendario;
    }

    private List<CalendarioSemanalDTO.SlotHorarioDTO> generarSlotsParaDia(DiaSemana dia) {
        List<CalendarioSemanalDTO.SlotHorarioDTO> slots = new ArrayList<>();

        LocalTime horaActual = HORA_INICIO;
        while (horaActual.isBefore(HORA_FIN)) {
            LocalTime horaFin = horaActual.plusMinutes(DURACION_SLOT_MINUTOS);
            int capacidadMaxima = slotConfigService != null
                    ? slotConfigService.getCapacidadMaxima(dia, horaActual, CAPACIDAD_MAXIMA_POR_SLOT)
                    : CAPACIDAD_MAXIMA_POR_SLOT;
            CalendarioSemanalDTO.SlotHorarioDTO slot = new CalendarioSemanalDTO.SlotHorarioDTO(
                    horaActual, horaFin, capacidadMaxima);
            slots.add(slot);
            horaActual = horaFin;
        }

        return slots;
    }

    private void asignarUsuariosASlots(CalendarioSemanalDTO calendario, List<Usuario> usuarios) {
        for (Usuario usuario : usuarios) {
            if (usuario.getDiasHorariosAsistencia() != null) {
                for (DiaHorarioAsistencia horario : usuario.getDiasHorariosAsistencia()) {
                    asignarUsuarioASlot(calendario, usuario, horario);
                }
            }
        }
    }

    private void asignarUsuarioASlot(CalendarioSemanalDTO calendario, Usuario usuario, DiaHorarioAsistencia horario) {
        List<CalendarioSemanalDTO.SlotHorarioDTO> slotsDelDia = calendario.getSlotsPorDia().get(horario.getDia());

        if (slotsDelDia != null) {
            for (CalendarioSemanalDTO.SlotHorarioDTO slot : slotsDelDia) {
                // Asignar solo si el slot coincide exactamente con el horario guardado
                if (slot.getHoraInicio().equals(horario.getHoraEntrada())
                        && slot.getHoraFin().equals(horario.getHoraSalida())) {
                    if (slot.isDisponible()) {
                        slot.getUsuariosAsignados().add(usuario);
                        slot.setCapacidadActual(slot.getUsuariosAsignados().size());
                    }
                }
            }
        }
    }

    public List<Usuario> obtenerUsuariosEnHorario(DiaSemana dia, LocalTime horaInicio, LocalTime horaFin) {
        List<Usuario> usuarios = usuarioRepository.findByTipoAsistencia(com.mattfuncional.enums.TipoAsistencia.PRESENCIAL);

        return usuarios.stream()
                .filter(usuario -> usuario.getDiasHorariosAsistencia() != null)
                .filter(usuario -> usuario.getDiasHorariosAsistencia().stream()
                        .anyMatch(horario -> horario.getDia().equals(dia)
                                && horario.getHoraEntrada().equals(horaInicio)
                                && horario.getHoraSalida().equals(horaFin)))
                .collect(Collectors.toList());
    }

    public boolean verificarDisponibilidad(DiaSemana dia, LocalTime horaInicio, LocalTime horaFin) {
        List<Usuario> usuariosEnHorario = obtenerUsuariosEnHorario(dia, horaInicio, horaFin);
        return usuariosEnHorario.size() < CAPACIDAD_MAXIMA_POR_SLOT;
    }

    public Map<String, Object> obtenerEstadisticasSemana(LocalDate fecha, Long profesorId) {
        CalendarioSemanalDTO calendario = generarCalendarioSemanal(fecha, profesorId);
        Map<String, Object> estadisticas = new HashMap<>();

        List<Usuario> usuariosPresenciales;
        if (profesorId != null) {
            usuariosPresenciales = usuarioRepository.findByProfesor_IdAndRol(profesorId, "USER");
        } else {
            usuariosPresenciales = usuarioRepository.findByTipoAsistencia(com.mattfuncional.enums.TipoAsistencia.PRESENCIAL);
        }
        int totalUsuarios = usuariosPresenciales.size();

        int slotsOcupados = 0;
        int slotsDisponibles = 0;
        List<Integer> capacidades = new ArrayList<>();
        for (List<CalendarioSemanalDTO.SlotHorarioDTO> slots : calendario.getSlotsPorDia().values()) {
            for (CalendarioSemanalDTO.SlotHorarioDTO slot : slots) {
                capacidades.add(slot.getCapacidadMaxima());
                if (slot.getCapacidadActual() > 0) {
                    slotsOcupados++;
                } else {
                    slotsDisponibles++;
                }
            }
        }
        // Obtener la capacidad máxima más frecuente (moda) o el primer valor si la
        // lista está vacía
        int capacidadMaxima = capacidades.isEmpty() ? CAPACIDAD_MAXIMA_POR_SLOT : capacidades.get(0);
        if (!capacidades.isEmpty()) {
            capacidadMaxima = capacidades.stream()
                    .collect(java.util.stream.Collectors.groupingBy(e -> e, java.util.stream.Collectors.counting()))
                    .entrySet().stream().max(java.util.Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey).orElse(CAPACIDAD_MAXIMA_POR_SLOT);
        }
        estadisticas.put("totalUsuarios", totalUsuarios);
        estadisticas.put("slotsOcupados", slotsOcupados);
        estadisticas.put("slotsDisponibles", slotsDisponibles);
        estadisticas.put("capacidadMaxima", capacidadMaxima);

        int totalLugares = 0;
        int totalOcupados = 0;
        int cantidadSlots = 0;
        for (Map.Entry<DiaSemana, List<CalendarioSemanalDTO.SlotHorarioDTO>> entry : calendario.getSlotsPorDia()
                .entrySet()) {
            for (CalendarioSemanalDTO.SlotHorarioDTO slot : entry.getValue()) {
                totalLugares += slot.getCapacidadMaxima();
                totalOcupados += slot.getCapacidadActual();
                cantidadSlots++;
            }
        }
        // Capacidad máxima global (por día) x cantidad de slots en la semana
        int capacidadMaximaGlobal = capacidadMaxima * cantidadSlots;
        double porcentajeOcupacion = capacidadMaximaGlobal == 0 ? 0
                : (double) totalOcupados / capacidadMaximaGlobal * 100;
        estadisticas.put("porcentajeOcupacion", porcentajeOcupacion);

        return estadisticas;
    }

    public List<DiaSemana> obtenerDiasDisponibles() {
        return Arrays.asList(DiaSemana.values());
    }

    public List<LocalTime> obtenerHorariosDisponibles() {
        List<LocalTime> horarios = new ArrayList<>();
        LocalTime horaActual = HORA_INICIO;

        while (horaActual.isBefore(HORA_FIN)) {
            horarios.add(horaActual);
            horaActual = horaActual.plusMinutes(DURACION_SLOT_MINUTOS);
        }

        return horarios;
    }

    public SlotConfigService getSlotConfigService() {
        return slotConfigService;
    }
}