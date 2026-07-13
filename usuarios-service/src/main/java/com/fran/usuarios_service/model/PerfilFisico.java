package com.fran.usuarios_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "perfil_fisico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilFisico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/*
	 * No se usa @ManyToOne aqui a proposito, guardamos solo el id; Usuario y
	 * PerfilFisico viven en la misma base de datos, pero mantenemos el mismo estilo
	 * simple (solo el id) que usaremos luego entre microservicios
	 */

	@NotNull(message = "El usuario es obligatorio")
	@Column(name = "usuario_id")
	private Long usuarioId;

	@NotNull(message = "El peso es obligatorio")
	@Positive(message = "El peso debe ser mayor que 0")
	private Double peso;

	@NotNull(message = "La altura es obligatoria")
	@Positive(message = "La altura debe ser mayor que 0")
	private Double altura;

	private LocalDate fechaRegistro;

	// JPA: asigna la fecha de registro automaticamente al crear el perfil
	
	@PrePersist
	public void prePersist() {
		this.fechaRegistro = LocalDate.now();
	}
}