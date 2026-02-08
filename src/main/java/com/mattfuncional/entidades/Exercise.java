package com.mattfuncional.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;
import com.mattfuncional.enums.MuscleGroup;

@Entity
@Table(uniqueConstraints = { 
    @UniqueConstraint(columnNames = { "name", "profesor_id" }, 
                      name = "uk_exercise_name_profesor") 
})
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ElementCollection
    private Set<MuscleGroup> muscleGroups;

    private String type;
    private String videoUrl;
    private String instructions;
    private String benefits;
    private String contraindications;

    @OneToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "imagen_id")
    @JsonIgnore
    private Imagen imagen;

    @ManyToOne
    @JoinColumn(name = "profesor_id", nullable = true)
    private Profesor profesor; // null = ejercicio predeterminado
    
    @Column(nullable = false)
    private Boolean esPredeterminado = false;

    // Constructor sin argumentos
    public Exercise() {
    }

    // Constructor con argumentos
    public Exercise(String name, String description, Set<MuscleGroup> muscleGroups, String type,
            String videoUrl, String instructions, String benefits, String contraindications) {
        this.name = name;
        this.description = description;
        this.muscleGroups = muscleGroups;
        this.type = type;
        this.videoUrl = videoUrl;
        this.instructions = instructions;
        this.benefits = benefits;
        this.contraindications = contraindications;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<MuscleGroup> getMuscleGroups() {
        return muscleGroups;
    }

    public void setMuscleGroups(Set<MuscleGroup> muscleGroups) {
        this.muscleGroups = muscleGroups;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getContraindications() {
        return contraindications;
    }

    public void setContraindications(String contraindications) {
        this.contraindications = contraindications;
    }

    public Imagen getImagen() {
        return imagen;
    }

    public void setImagen(Imagen imagen) {
        this.imagen = imagen;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }
    
    public Boolean getEsPredeterminado() {
        return esPredeterminado;
    }
    
    public void setEsPredeterminado(Boolean esPredeterminado) {
        this.esPredeterminado = esPredeterminado;
    }
    
    /**
     * Verifica si el ejercicio es predeterminado
     * Un ejercicio es predeterminado si tiene el flag activado o si no tiene profesor asignado
     */
    public boolean isPredeterminado() {
        return esPredeterminado != null && esPredeterminado || profesor == null;
    }
    
    /**
     * Verifica si el ejercicio puede ser editado por un usuario específico
     * - Admin puede editar todo
     * - Ejercicios predeterminados solo pueden ser editados por admin
     * - Ejercicios personalizados solo pueden ser editados por su propietario
     */
    public boolean puedeSerEditadoPor(com.mattfuncional.entidades.Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        
        // El profesor (único gestor del panel) puede editar todo, incluidos predeterminados
        if ("PROFESOR".equals(usuario.getRol())) {
            return true;
        }
        
        // Si es predeterminado, solo el profesor puede editar (ya cubierto arriba)
        if (isPredeterminado()) {
            return false;
        }
        
        // Si tiene profesor, solo el propietario puede editar
        if (profesor != null && usuario.getProfesor() != null) {
            return profesor.getId().equals(usuario.getProfesor().getId());
        }
        
        return false;
    }
}
