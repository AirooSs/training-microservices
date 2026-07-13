package com.fran.entrenamientos_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Tabla intermedia que resuelve la relacion muchos a muchos entre Entrenamiento y Ejercicio
// Guarda ademas datos propios de esa combinacion (repeticiones, peso)

@Entity
@Table(name = "entrenamiento_ejercicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenamientoEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El entrenamiento es obligatorio")
    @ManyToOne
    @JoinColumn(name = "entrenamiento_id")
    private Entrenamiento entrenamiento;

    @NotNull(message = "El ejercicio es obligatorio")
    @ManyToOne
    @JoinColumn(name = "ejercicio_id")
    private Ejercicio ejercicio;

    @Positive(message = "Las repeticiones deben ser mayores que 0")
    private Integer repeticiones;

    private Double peso;

    private Integer orden;
}
