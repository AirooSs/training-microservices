package com.fran.entrenamientos_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

// DTO (Data Transfer Object): representa el JSON que llega en la peticion,
// no una entidad de base de datos. Se usa aqui porque el cliente solo
// manda los ids de entrenamiento y ejercicio, no los objetos completos

@Data
public class AgregarEjercicioRequest {

    @NotNull(message = "El id del entrenamiento es obligatorio")
    private Long entrenamientoId;

    @NotNull(message = "El id del ejercicio es obligatorio")
    private Long ejercicioId;

    @Positive(message = "Las repeticiones deben ser mayores que 0")
    private Integer repeticiones;

    private Double peso;

    private Integer orden;
}