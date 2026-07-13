package com.fran.usuarios_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;

	@NotBlank(message = "El email es obligatorio")
	@Email(message = "El email no tiene un formato valido")
	@Column(unique = true)
	private String email;

	@NotNull(message = "La fecha de nacimiento es obligatoria")
	private LocalDate fechaNacimiento;

	@Enumerated(EnumType.STRING)
	private Categoria categoria;

	private LocalDateTime fechaAlta;

	/*
	 * El PrePersist se ejecuta automáticamente antes de guardar el usuario por primera vez en la
	 * base de datos.
	 * Rellena la fecha de alta sin necesidad de asignarla manualmente
	 * en el controlador o el servicio
	 */
	
	@PrePersist
	public void prePersist() {
		this.fechaAlta = LocalDateTime.now();
	}

	public enum Categoria {
		PRINCIPIANTE, INTERMEDIO, AVANZADO
	}
}