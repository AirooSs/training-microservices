package com.fran.entrenamientos_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "entrenamiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entrenamiento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El nombre del entrenamiento es obligatorio")
	private String nombre;

	@NotNull(message = "El tipo de entrenamiento es obligatorio")
	@Enumerated(EnumType.STRING)
	private TipoEntrenamiento tipoEntrenamiento;

	private Integer duracionMinutos;

	@NotNull(message = "La fecha es obligatoria")
	private LocalDate fecha;

	public enum TipoEntrenamiento {
		FUERZA, HIPERTROFIA, CARDIO
	}
}
