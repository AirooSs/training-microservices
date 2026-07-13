package com.fran.entrenamientos_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RegistrarPrRequest {

	@NotNull(message = "El id del usuario es obligatorio")
	private Long usuarioId;

	@NotNull(message = "El id del ejercicio es obligatorio")
	private Long ejercicioId;

	@NotNull(message = "El peso levantado es obligatorio")
	@Positive(message = "El peso levantado debe ser mayor que 0")
	private Double pesoLevantado;
}
