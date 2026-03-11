package com.mattfuncional.servicios;

import com.mattfuncional.entidades.DiaHorarioAsistencia;
import com.mattfuncional.entidades.MedicionFisica;
import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.enums.DiaSemana;
import com.mattfuncional.enums.TipoAsistencia;
import com.mattfuncional.repositorios.MedicionFisicaRepository;
import com.mattfuncional.repositorios.RutinaRepository;
import com.mattfuncional.repositorios.UsuarioRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exporta alumnos del profesor a Excel según documentación EXPORTACION_ALUMNOS_EXCEL.md.
 * Incluye: datos del alumno, cantidad de asignaciones, últimas 3 evoluciones (mediciones físicas).
 * No incluye: fecha de alta, asistencias presentes/ausentes, listado de rutinas asignadas.
 */
@Service
public class AlumnoExportService {

    private static final DateTimeFormatter FECHA_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final int ULTIMAS_EVOLUCIONES = 3;

    private final UsuarioRepository usuarioRepository;
    private final MedicionFisicaRepository medicionFisicaRepository;
    private final RutinaRepository rutinaRepository;

    public AlumnoExportService(UsuarioRepository usuarioRepository,
                               MedicionFisicaRepository medicionFisicaRepository,
                               RutinaRepository rutinaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.medicionFisicaRepository = medicionFisicaRepository;
        this.rutinaRepository = rutinaRepository;
    }

    /**
     * Genera un Excel con los alumnos del profesor. Retorna el archivo como byte[].
     */
    @Transactional(readOnly = true)
    public byte[] exportarAlumnosAExcel(Long profesorId) throws Exception {
        List<Usuario> alumnos = usuarioRepository.findByProfesor_IdAndRol(profesorId, "ALUMNO");
        if (alumnos == null) alumnos = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Alumnos");
            CellStyle headerStyle = crearEstiloCabecera(workbook);
            CellStyle dateStyle = crearEstiloFecha(workbook);

            int rowNum = 0;

            // Cabecera
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = getHeaders();
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (Usuario u : alumnos) {
                Row row = sheet.createRow(rowNum++);
                int col = 0;

                // 1. Nombre
                row.createCell(col++).setCellValue(toString(u.getNombre()));
                // 2. Correo
                row.createCell(col++).setCellValue(toString(u.getCorreo()));
                // 3. Celular
                row.createCell(col++).setCellValue(toString(u.getCelular()));
                // 4. Edad
                row.createCell(col++).setCellValue(u.getEdad());
                // 5. Sexo
                row.createCell(col++).setCellValue(toString(u.getSexo()));
                // 6. Peso actual
                row.createCell(col++).setCellValue(u.getPeso());
                // 7. Estado
                row.createCell(col++).setCellValue(toString(u.getEstadoAlumno()));
                // 8. Fecha inicio
                col = setCellFecha(row, col, u.getFechaInicio(), dateStyle);
                // 9. Fecha baja
                col = setCellFecha(row, col, u.getFechaBaja(), dateStyle);
                // 10. Tipo de asistencia (Virtual / Presencial / Semipresencial)
                row.createCell(col++).setCellValue(formatearTipoAsistencia(u.getTipoAsistencia()));
                // 11. Detalle asistencia
                row.createCell(col++).setCellValue(toString(u.getDetalleAsistencia()));
                // 12. Días y horarios (solo si Presencial o Semipresencial)
                row.createCell(col++).setCellValue(formatearDiasYHorarios(u));
                // 13-17. Objetivos, restricciones, notas, contacto emergencia x2
                row.createCell(col++).setCellValue(toString(u.getObjetivosPersonales()));
                row.createCell(col++).setCellValue(toString(u.getRestriccionesMedicas()));
                row.createCell(col++).setCellValue(toString(u.getNotasProfesor()));
                row.createCell(col++).setCellValue(toString(u.getContactoEmergenciaNombre()));
                row.createCell(col++).setCellValue(toString(u.getContactoEmergenciaTelefono()));
                // 18. Cantidad de asignaciones
                int countRutinas = rutinaRepository.findByUsuarioIdAndEsPlantillaFalse(u.getId()).size();
                row.createCell(col++).setCellValue(countRutinas);

                // Últimas 3 evoluciones
                List<MedicionFisica> mediciones = medicionFisicaRepository.findByUsuario_IdOrderByFechaDesc(u.getId());
                List<MedicionFisica> ultimas = mediciones.isEmpty() ? List.of()
                        : mediciones.subList(0, Math.min(ULTIMAS_EVOLUCIONES, mediciones.size()));

                for (int ev = 1; ev <= ULTIMAS_EVOLUCIONES; ev++) {
                    MedicionFisica m = (ev <= ultimas.size()) ? ultimas.get(ev - 1) : null;
                    col = setCellFecha(row, col, m != null ? m.getFecha() : null, dateStyle);
                    col = setCellDouble(row, col, m != null ? m.getPeso() : null);
                    col = setCellDouble(row, col, m != null ? m.getAltura() : null);
                    col = setCellDouble(row, col, m != null ? m.getCintura() : null);
                    col = setCellDouble(row, col, m != null ? m.getPecho() : null);
                    col = setCellDouble(row, col, m != null ? m.getCadera() : null);
                    col = setCellDouble(row, col, m != null ? m.getBiceps() : null);
                    col = setCellDouble(row, col, m != null ? m.getMuslo() : null);
                }
            }

            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private static String[] getHeaders() {
        return new String[]{
                "Nombre", "Correo", "Celular", "Edad", "Sexo", "Peso actual", "Estado",
                "Fecha inicio", "Fecha baja", "Tipo de asistencia", "Detalle asistencia", "Días y horarios",
                "Objetivos personales", "Restricciones médicas", "Notas profesor",
                "Contacto emergencia nombre", "Contacto emergencia teléfono",
                "Cantidad de asignaciones",
                "Ev1_fecha", "Ev1_peso", "Ev1_altura", "Ev1_cintura", "Ev1_pecho", "Ev1_cadera", "Ev1_biceps", "Ev1_muslo",
                "Ev2_fecha", "Ev2_peso", "Ev2_altura", "Ev2_cintura", "Ev2_pecho", "Ev2_cadera", "Ev2_biceps", "Ev2_muslo",
                "Ev3_fecha", "Ev3_peso", "Ev3_altura", "Ev3_cintura", "Ev3_pecho", "Ev3_cadera", "Ev3_biceps", "Ev3_muslo"
        };
    }

    private static String toString(Object o) {
        return o == null ? "" : String.valueOf(o).trim();
    }

    private static String formatearTipoAsistencia(TipoAsistencia t) {
        if (t == null) return "";
        return switch (t) {
            case ONLINE -> "Virtual";
            case PRESENCIAL -> "Presencial";
            case SEMIPRESENCIAL -> "Semipresencial";
        };
    }

    /**
     * Solo para Presencial o Semipresencial. Formato: "Lunes 10:00-11:00, Martes 13:00-14:00" o similar.
     */
    private static String formatearDiasYHorarios(Usuario u) {
        if (u.getTipoAsistencia() != TipoAsistencia.PRESENCIAL && u.getTipoAsistencia() != TipoAsistencia.SEMIPRESENCIAL) {
            return "";
        }
        List<DiaHorarioAsistencia> dias = u.getDiasHorariosAsistencia();
        if (dias == null || dias.isEmpty()) return "";
        return dias.stream()
                .map(d -> nombreDia(d.getDia()) + " " + (d.getHoraEntrada() != null ? d.getHoraEntrada().format(HORA_FORMAT) : "")
                        + (d.getHoraSalida() != null ? "-" + d.getHoraSalida().format(HORA_FORMAT) : ""))
                .collect(Collectors.joining(", "));
    }

    private static String nombreDia(DiaSemana d) {
        if (d == null) return "";
        return switch (d) {
            case LUNES -> "Lunes";
            case MARTES -> "Martes";
            case MIERCOLES -> "Miércoles";
            case JUEVES -> "Jueves";
            case VIERNES -> "Viernes";
            case SABADO -> "Sábado";
            case DOMINGO -> "Domingo";
        };
    }

    private static int setCellFecha(Row row, int col, LocalDate fecha, CellStyle dateStyle) {
        Cell cell = row.createCell(col++);
        if (fecha != null) {
            cell.setCellValue(fecha.format(FECHA_FORMAT));
            cell.setCellStyle(dateStyle);
        }
        return col;
    }

    private static int setCellDouble(Row row, int col, Double value) {
        Cell cell = row.createCell(col++);
        if (value != null) cell.setCellValue(value);
        return col;
    }

    private static CellStyle crearEstiloCabecera(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        s.setFont(f);
        return s;
    }

    private static CellStyle crearEstiloFecha(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setDataFormat(wb.createDataFormat().getFormat("dd/mm/yyyy"));
        return s;
    }
}
