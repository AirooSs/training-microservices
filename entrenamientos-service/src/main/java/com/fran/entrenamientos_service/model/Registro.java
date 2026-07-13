package com.fran.entrenamientos_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referencia externa a Usuario, que vive en usuarios-service (otra base de datos)
    // No se puede usar @ManyToOne aqui, por eso guardamos solo el id
    
    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "usuario_id")
    private Long usuarioId;

    @NotNull(message = "El entrenamiento es obligatorio")
    @ManyToOne
    @JoinColumn(name = "entrenamiento_id")
    private Entrenamiento entrenamiento;

    private LocalDateTime fecha;

    // Series y repeticiones realizadas, aplicable a entrenamientos de fuerza e hipertrofia
    private Integer series;

    private Integer repeticiones;

    // Peso utilizado, aplicable a entrenamientos de fuerza e hipertrofia
    private Double peso;

    // Duracion en minutos, aplicable sobre todo a entrenamientos de cardio
    private Integer duracionMinutos;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }
}