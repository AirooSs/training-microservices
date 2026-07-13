package com.fran.entrenamientos_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ejercicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del ejercicio es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo de ejercicio es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoEjercicio tipoEjercicio;

    public enum TipoEjercicio {
    	 EMPUJE, TRACCION, PIERNA
    }
}