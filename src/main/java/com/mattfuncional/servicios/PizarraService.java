package com.mattfuncional.servicios;

import com.mattfuncional.dto.PizarraEstadoDTO;
import com.mattfuncional.entidades.Exercise;
import com.mattfuncional.entidades.GrupoMuscular;
import com.mattfuncional.entidades.Pizarra;
import com.mattfuncional.entidades.PizarraColumna;
import com.mattfuncional.entidades.PizarraItem;
import com.mattfuncional.entidades.Profesor;
import com.mattfuncional.repositorios.ExerciseRepository;
import com.mattfuncional.repositorios.PizarraColumnaRepository;
import com.mattfuncional.repositorios.PizarraItemRepository;
import com.mattfuncional.repositorios.PizarraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PizarraService {

    private static final SecureRandom TOKEN_RANDOM = new SecureRandom();
    private static final char[] TOKEN_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final PizarraRepository pizarraRepository;
    private final PizarraColumnaRepository columnaRepository;
    private final PizarraItemRepository itemRepository;
    private final ExerciseRepository exerciseRepository;

    public PizarraService(PizarraRepository pizarraRepository,
                          PizarraColumnaRepository columnaRepository,
                          PizarraItemRepository itemRepository,
                          ExerciseRepository exerciseRepository) {
        this.pizarraRepository = pizarraRepository;
        this.columnaRepository = columnaRepository;
        this.itemRepository = itemRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public List<Pizarra> listarPorProfesor(Long profesorId) {
        return pizarraRepository.findByProfesorIdOrderByFechaModificacionDesc(profesorId);
    }

    public Optional<Pizarra> obtenerPorId(Long id) {
        return pizarraRepository.findByIdWithColumnas(id);
    }

    public Optional<Pizarra> obtenerPorToken(String token) {
        return pizarraRepository.findByToken(token);
    }

    /**
     * Crea una nueva pizarra con N columnas vacías.
     */
    public Pizarra crear(Profesor profesor, String nombre, int cantidadColumnas) {
        if (cantidadColumnas < 1 || cantidadColumnas > 6) {
            throw new IllegalArgumentException("Cantidad de columnas debe ser entre 1 y 6");
        }
        Pizarra p = new Pizarra();
        p.setProfesor(profesor);
        p.setNombre(nombre != null && !nombre.isBlank() ? nombre.trim() : "Pizarra");
        p.setCantidadColumnas(cantidadColumnas);
        p.setToken(generarTokenUnico());
        p = pizarraRepository.save(p);

        for (int i = 0; i < cantidadColumnas; i++) {
            PizarraColumna col = new PizarraColumna();
            col.setPizarra(p);
            col.setTitulo("");
            col.setOrden(i);
            columnaRepository.save(col);
        }
        return pizarraRepository.findById(p.getId()).orElse(p);
    }

    /**
     * Actualiza nombre y títulos de columnas.
     */
    public Pizarra actualizarBasico(Long id, String nombre, List<String> titulos, Long profesorId) {
        Pizarra p = pizarraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pizarra no encontrada"));
        if (!p.getProfesor().getId().equals(profesorId)) {
            throw new RuntimeException("No tiene permisos para editar esta pizarra");
        }
        if (nombre != null) p.setNombre(nombre.trim());
        if (titulos != null && !titulos.isEmpty()) {
            List<PizarraColumna> cols = columnaRepository.findByPizarraIdOrderByOrdenAsc(p.getId());
            for (int i = 0; i < Math.min(titulos.size(), cols.size()); i++) {
                String titulo = titulos.get(i);
                cols.get(i).setTitulo(titulo != null ? titulo.trim() : "");
            }
            columnaRepository.saveAll(cols);
        }
        return pizarraRepository.save(p);
    }

    /**
     * Agrega un item a una columna.
     */
    public PizarraItem agregarItem(Long columnaId, Long exerciseId, Integer peso, Integer repeticiones, String unidad, Long profesorId) {
        PizarraColumna col = columnaRepository.findById(columnaId)
                .orElseThrow(() -> new RuntimeException("Columna no encontrada"));
        if (!col.getPizarra().getProfesor().getId().equals(profesorId)) {
            throw new RuntimeException("No tiene permisos");
        }
        Exercise ex = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));
        int maxOrden = col.getItems().stream().mapToInt(PizarraItem::getOrden).max().orElse(-1);
        PizarraItem item = new PizarraItem();
        item.setColumna(col);
        item.setExercise(ex);
        item.setPeso(peso);
        item.setRepeticiones(repeticiones);
        item.setUnidad(unidad != null ? unidad : "reps");
        item.setOrden(maxOrden + 1);
        return itemRepository.save(item);
    }

    /**
     * Actualiza peso y repeticiones de un item.
     */
    public void actualizarItem(Long itemId, Integer peso, Integer repeticiones, String unidad, Long profesorId) {
        PizarraItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        if (!item.getColumna().getPizarra().getProfesor().getId().equals(profesorId)) {
            throw new RuntimeException("No tiene permisos");
        }
        if (peso != null) item.setPeso(peso);
        if (repeticiones != null) item.setRepeticiones(repeticiones);
        if (unidad != null) item.setUnidad(unidad);
        itemRepository.save(item);
    }

    /**
     * Elimina un item.
     */
    public void eliminarItem(Long itemId, Long profesorId) {
        PizarraItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        if (!item.getColumna().getPizarra().getProfesor().getId().equals(profesorId)) {
            throw new RuntimeException("No tiene permisos");
        }
        itemRepository.delete(item);
    }

    /**
     * Reordena items dentro de una columna.
     */
    public void reordenarItems(Long columnaId, List<Long> itemIdsEnOrden, Long profesorId) {
        PizarraColumna col = columnaRepository.findById(columnaId)
                .orElseThrow(() -> new RuntimeException("Columna no encontrada"));
        if (!col.getPizarra().getProfesor().getId().equals(profesorId)) {
            throw new RuntimeException("No tiene permisos");
        }
        for (int i = 0; i < itemIdsEnOrden.size(); i++) {
            final int orden = i;
            Long itemId = itemIdsEnOrden.get(i);
            itemRepository.findById(itemId).ifPresent(item -> {
                if (item.getColumna().getId().equals(columnaId)) {
                    item.setOrden(orden);
                    itemRepository.save(item);
                }
            });
        }
    }

    /**
     * Mueve un item de una columna a otra.
     */
    public void moverItem(Long itemId, Long columnaDestinoId, int ordenDestino, Long profesorId) {
        PizarraItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        PizarraColumna colDest = columnaRepository.findById(columnaDestinoId)
                .orElseThrow(() -> new RuntimeException("Columna destino no encontrada"));
        if (!item.getColumna().getPizarra().getProfesor().getId().equals(profesorId) ||
            !colDest.getPizarra().getId().equals(item.getColumna().getPizarra().getId())) {
            throw new RuntimeException("No tiene permisos o columnas no pertenecen a la misma pizarra");
        }
        item.setColumna(colDest);
        item.setOrden(ordenDestino);
        itemRepository.save(item);
    }

    /**
     * Construye el DTO para la API de sala (vista TV).
     */
    public PizarraEstadoDTO construirEstadoParaSala(String token) {
        Pizarra p = pizarraRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Pizarra no encontrada"));
        PizarraEstadoDTO dto = new PizarraEstadoDTO();
        dto.setNombre(p.getNombre());
        dto.setCantidadColumnas(p.getCantidadColumnas());// Load columnas
        List<PizarraColumna> columnas = columnaRepository.findByPizarraIdOrderByOrdenAsc(p.getId());
        for (PizarraColumna col : columnas) {
            PizarraEstadoDTO.ColumnaDTO colDto = new PizarraEstadoDTO.ColumnaDTO();
            colDto.setId(col.getId());
            colDto.setTitulo(col.getTitulo());
            colDto.setOrden(col.getOrden());
            List<PizarraItem> items = itemRepository.findByColumnaIdOrderByOrdenAsc(col.getId());
            for (PizarraItem it : items) {
                PizarraEstadoDTO.ItemDTO itemDto = new PizarraEstadoDTO.ItemDTO();
                itemDto.setId(it.getId());
                itemDto.setExerciseId(it.getExercise().getId());
                itemDto.setEjercicioNombre(it.getExercise().getName());
                itemDto.setImagenUrl(it.getExercise().getImagen() != null ? it.getExercise().getImagen().getUrl() : "/img/not_imagen.png");
                String grupos = it.getExercise().getGrupos() != null
                        ? it.getExercise().getGrupos().stream().map(GrupoMuscular::getNombre).collect(Collectors.joining(", "))
                        : "";
                itemDto.setGrupoMuscular(grupos);
                itemDto.setPeso(it.getPeso());
                itemDto.setRepeticiones(it.getRepeticiones());
                itemDto.setUnidad(it.getUnidad());
                colDto.getItems().add(itemDto);
            }
            dto.getColumnas().add(colDto);
        }
        return dto;
    }

    private String generarTokenUnico() {
        String token;
        do {
            token = generarToken(12);
        } while (pizarraRepository.existsByToken(token));
        return token;
    }

    private String generarToken(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(TOKEN_CHARS[TOKEN_RANDOM.nextInt(TOKEN_CHARS.length)]);
        }
        return builder.toString();
    }

    /**
     * Agrega una columna a la pizarra (máximo 6).
     */
    public void agregarColumna(Long pizarraId, Long profesorId) {
        Pizarra p = pizarraRepository.findById(pizarraId)
                .orElseThrow(() -> new RuntimeException("Pizarra no encontrada"));
        if (!p.getProfesor().getId().equals(profesorId)) {
            throw new RuntimeException("No tiene permisos");
        }
        List<PizarraColumna> columnas = columnaRepository.findByPizarraIdOrderByOrdenAsc(p.getId());
        if (columnas.size() >= 6) {
            throw new IllegalArgumentException("Máximo 6 columnas");
        }
        PizarraColumna nueva = new PizarraColumna();
        nueva.setPizarra(p);
        nueva.setTitulo("");
        nueva.setOrden(columnas.size());
        columnaRepository.save(nueva);
        p.setCantidadColumnas(columnas.size() + 1);
        pizarraRepository.save(p);
    }

    /**
     * Quita una columna (y sus items). Mínimo 1 columna. Reordena el resto.
     */
    public void quitarColumna(Long columnaId, Long profesorId) {
        PizarraColumna col = columnaRepository.findById(columnaId)
                .orElseThrow(() -> new RuntimeException("Columna no encontrada"));
        Pizarra p = col.getPizarra();
        if (!p.getProfesor().getId().equals(profesorId)) {
            throw new RuntimeException("No tiene permisos");
        }
        List<PizarraColumna> columnas = columnaRepository.findByPizarraIdOrderByOrdenAsc(p.getId());
        if (columnas.size() <= 1) {
            throw new IllegalArgumentException("Debe haber al menos 1 columna");
        }
        columnaRepository.delete(col);
        List<PizarraColumna> restantes = columnaRepository.findByPizarraIdOrderByOrdenAsc(p.getId());
        for (int i = 0; i < restantes.size(); i++) {
            restantes.get(i).setOrden(i);
        }
        columnaRepository.saveAll(restantes);
        p.setCantidadColumnas(restantes.size());
        pizarraRepository.save(p);
    }

    public void eliminar(Long id, Long profesorId) {
        Pizarra p = pizarraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pizarra no encontrada"));
        if (!p.getProfesor().getId().equals(profesorId)) {
            throw new RuntimeException("No tiene permisos");
        }
        pizarraRepository.delete(p);
    }
}
