package com.fran.entrenamientos_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearRegistroRequest {

	@NotNull(message = "El id del usuario es obligatorio")
	private Long usuarioId;

	@NotNull(message = "El id del entrenamiento es obligatorio")
	private Long entrenamientoId;

	private Integer series;
	private Integer repeticiones;
	private Double peso;
	private Integer duracionMinutos;
}