package com.fran.entrenamientos_service.repository;

import com.fran.entrenamientos_service.model.PR;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PRRepository extends JpaRepository<PR, Long> {

	// Evolucion de PRs de un usuario mediante su fecha, para ver su progreso

	List<PR> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

	/*
	 * El PR mas reciente de un usuario en un ejercicio concreto (el peso mas alto)
	 * lo usaremos para comparar y saber si un nuevo registro es marca personal
	 */

	Optional<PR> findTopByUsuarioIdAndEjercicioIdOrderByPesoMaximoDesc(Long usuarioId, Long ejercicioId);
}