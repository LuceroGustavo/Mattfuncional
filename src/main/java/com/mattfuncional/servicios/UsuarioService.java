package com.mattfuncional.servicios;

import com.mattfuncional.entidades.Usuario;
import com.mattfuncional.entidades.Profesor;
import com.mattfuncional.repositorios.UsuarioRepository;
import com.mattfuncional.repositorios.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
    @Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProfesorRepository profesorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- MÉTODOS CON CACHÉ PARA FASE 3 ---

    @Cacheable(value = "usuarios", key = "#id")
    public Usuario getUsuarioById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "usuarios", key = "'all'")
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Cacheable(value = "usuarios", key = "'alumnos'")
    public List<Usuario> getAlumnos() {
        return usuarioRepository.findAllAlumnosIncludingOrphans();
    }

    @Cacheable(value = "usuarios", key = "'profesor-' + #profesorId")
    public List<Usuario> getAlumnosByProfesorId(Long profesorId) {
        return usuarioRepository.findAlumnosByProfesorIdWithRelations(profesorId);
    }

    @Cacheable(value = "usuarios", key = "'alumnos-sin-profesor'")
    public List<Usuario> getAlumnosSinProfesor() {
        return usuarioRepository.findAllAlumnosWithProfesor().stream()
            .filter(u -> u.getProfesor() == null)
            .collect(Collectors.toList());
    }

    @Cacheable(value = "usuarios", key = "'user-' + #id + '-with-relations'")
    public Optional<Usuario> getUsuarioByIdWithRelations(Long id) {
        return usuarioRepository.findByIdWithAllRelations(id);
    }

    /**
     * Carga un alumno con todas las relaciones necesarias para la ficha de detalle
     * (profesor, rutinas y horarios de asistencia). No usa caché para evitar entidades
     * desconectadas con colecciones lazy sin inicializar.
     */
    public Usuario getUsuarioByIdParaFicha(Long id) {
        Optional<Usuario> opt = usuarioRepository.findByIdWithAllRelations(id);
        if (opt.isEmpty()) return null;
        Usuario u = opt.get();
        Hibernate.initialize(u.getDiasHorariosAsistencia());
        return u;
    }

    // --- MÉTODOS CON EVICCIÓN DE CACHÉ ---

    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario crearAlumno(Usuario usuario) {
        usuario.setRol("ALUMNO");
        // Alumnos no usan login: no se guarda contraseña (queda null en BD)

        if (usuario.getEstadoAlumno() == null || usuario.getEstadoAlumno().trim().isEmpty()) {
            usuario.setEstadoAlumno("ACTIVO");
        }
        if (usuario.getFechaAlta() == null) {
            usuario.setFechaAlta(java.time.LocalDate.now());
        }
        if ("INACTIVO".equalsIgnoreCase(usuario.getEstadoAlumno()) && usuario.getFechaBaja() == null) {
            usuario.setFechaBaja(java.time.LocalDate.now());
        } else if ("ACTIVO".equalsIgnoreCase(usuario.getEstadoAlumno())) {
            usuario.setFechaBaja(null);
        }
        appendHistorialEstado(usuario, "ALTA");
        
        // Asignar avatar aleatorio si no tiene uno
        if (usuario.getAvatar() == null || usuario.getAvatar().trim().isEmpty()) {
            int avatarNumber = (int) (Math.random() * 8) + 1; // Número aleatorio entre 1 y 8
            usuario.setAvatar("/img/avatar" + avatarNumber + ".png");
        }
        
        return usuarioRepository.save(usuario);
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario actualizarUsuario(Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuario.getId()));

        // Actualizar campos básicos
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setEdad(usuario.getEdad());
        usuarioExistente.setSexo(usuario.getSexo());
        usuarioExistente.setPeso(usuario.getPeso());
        usuarioExistente.setCorreo(usuario.getCorreo());
        usuarioExistente.setTipoAsistencia(usuario.getTipoAsistencia());
        usuarioExistente.setDiasHorariosAsistencia(usuario.getDiasHorariosAsistencia());
        usuarioExistente.setCelular(usuario.getCelular());
        usuarioExistente.setNotasProfesor(usuario.getNotasProfesor());
        usuarioExistente.setObjetivosPersonales(usuario.getObjetivosPersonales());
        usuarioExistente.setRestriccionesMedicas(usuario.getRestriccionesMedicas());
        usuarioExistente.setContactoEmergenciaNombre(usuario.getContactoEmergenciaNombre());
        usuarioExistente.setContactoEmergenciaTelefono(usuario.getContactoEmergenciaTelefono());
        if (usuarioExistente.getFechaAlta() == null) {
            usuarioExistente.setFechaAlta(java.time.LocalDate.now());
        }

        String estadoAnterior = usuarioExistente.getEstadoAlumno();
        String nuevoEstado = usuario.getEstadoAlumno();
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            nuevoEstado = estadoAnterior != null ? estadoAnterior : "ACTIVO";
        }
        usuarioExistente.setEstadoAlumno(nuevoEstado);

        if (estadoAnterior == null || !estadoAnterior.equalsIgnoreCase(nuevoEstado)) {
            if ("INACTIVO".equalsIgnoreCase(nuevoEstado)) {
                usuarioExistente.setFechaBaja(java.time.LocalDate.now());
                appendHistorialEstado(usuarioExistente, "BAJA");
            } else if ("ACTIVO".equalsIgnoreCase(nuevoEstado)) {
                usuarioExistente.setFechaBaja(null);
                appendHistorialEstado(usuarioExistente, "REACTIVADO");
            }
        }

        // Actualizar relaciones
        if (usuario.getProfesor() != null) {
            usuarioExistente.setProfesor(usuario.getProfesor());
        }

        // IMPORTANTE: No tocar las colecciones que tienen orphanRemoval
        // Las medicionesFisicas y rutinas se mantienen como están
        // Solo se actualizan si explícitamente se proporcionan nuevas listas

        return usuarioRepository.save(usuarioExistente);
    }

    private void appendHistorialEstado(Usuario usuario, String evento) {
        String fecha = java.time.LocalDate.now().toString();
        String linea = fecha + " - " + evento;
        String historial = usuario.getHistorialEstado();
        if (historial == null || historial.trim().isEmpty()) {
            usuario.setHistorialEstado(linea);
        } else {
            usuario.setHistorialEstado(historial + "\n" + linea);
        }
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    /**
     * Asigna avatares a usuarios existentes que no tienen uno
     */
    @CacheEvict(value = "usuarios", allEntries = true)
    public void asignarAvataresAUsuariosExistentes() {
        System.out.println("=== Iniciando asignación de avatares ===");
        
        List<Usuario> todosLosUsuarios = usuarioRepository.findAll();
        System.out.println("Total de usuarios encontrados: " + todosLosUsuarios.size());
        
        List<Usuario> usuariosSinAvatar = todosLosUsuarios.stream()
                .filter(u -> u.getAvatar() == null || 
                           u.getAvatar().trim().isEmpty() || 
                           u.getAvatar().equals("/img/not_imagen.png"))
                .collect(Collectors.toList());
        
        System.out.println("Usuarios sin avatar válido: " + usuariosSinAvatar.size());
        
        for (Usuario usuario : usuariosSinAvatar) {
            int avatarNumber = (int) (Math.random() * 8) + 1; // Número aleatorio entre 1 y 8
            String avatarPath = "/img/avatar" + avatarNumber + ".png";
            usuario.setAvatar(avatarPath);
            usuarioRepository.save(usuario);
            System.out.println("Avatar asignado a " + usuario.getNombre() + " (ID: " + usuario.getId() + "): " + avatarPath);
        }
        
        System.out.println("=== Asignación de avatares completada ===");
    }

    // --- MÉTODOS EXISTENTES (sin cambios) ---

    public Usuario getUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String correo = authentication.getName();
            Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
            
            // Si es un profesor y no tiene la relación cargada, intentar cargarla
            if (usuario != null && "ADMIN".equals(usuario.getRol()) && usuario.getProfesor() == null) {
                try {
                    Profesor profesor = profesorRepository.findByCorreo(correo);
                    if (profesor != null) {
                        usuario.setProfesor(profesor);
                    }
                } catch (Exception e) {
                    // Si hay error al cargar el profesor, continuar sin él
                }
            }
            
            return usuario;
        }
        return null;
    }

    /**
     * Obtiene el usuario actual con todas las relaciones cargadas (profesor, rutinas, etc.)
     */
    public Usuario getUsuarioActualWithRelations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String correo = authentication.getName();
            // Usar la consulta optimizada que carga las relaciones
            return usuarioRepository.findByIdWithAllRelations(
                usuarioRepository.findByCorreo(correo).map(Usuario::getId).orElse(null)
            ).orElse(null);
        }
        return null;
    }


    public void actualizarPasswordDeUsuario(Usuario usuario, String nuevaPassword) {
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }

    /**
     * Verifica si una contraseña coincide con la contraseña encriptada del usuario
     */
    public boolean verificarPassword(String passwordPlana, String passwordEncriptada) {
        return passwordEncoder.matches(passwordPlana, passwordEncriptada);
    }

    /**
     * Busca un profesor por su correo electrónico
     */
    public Profesor findProfesorByCorreo(String correo) {
        return profesorRepository.findByCorreo(correo);
    }

    /**
     * Crea un usuario para un profesor
     */
    @Transactional
    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario crearUsuarioParaProfesor(Profesor profesor, String password) {
        Usuario usuario = new Usuario();
        usuario.setNombre(profesor.getNombre() + (profesor.getApellido() != null && !profesor.getApellido().isEmpty() ? " " + profesor.getApellido() : ""));
        usuario.setCorreo(profesor.getCorreo());
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol("ADMIN");
        // Establecer la relación con el profesor para que pueda acceder a sus ejercicios
        usuario.setProfesor(profesor);
        
        // Asignar avatar aleatorio
        int avatarNumber = (int) (Math.random() * 8) + 1;
        usuario.setAvatar("/img/avatar" + avatarNumber + ".png");
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        // Verificar que la relación se estableció correctamente
        if (usuarioGuardado.getProfesor() == null) {
            throw new RuntimeException("Error al establecer la relación con el profesor para el usuario: " + usuarioGuardado.getCorreo());
        }
        
        return usuarioGuardado;
    }
}
