package com.fran.entrenamientos_service.repository;

import com.fran.entrenamientos_service.model.EntrenamientoEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntrenamientoEjercicioRepository extends JpaRepository<EntrenamientoEjercicio, Long> {

	// Todos los ejercicios que componen un entrenamiento concreto
	List<EntrenamientoEjercicio> findByEntrenamientoId(Long entrenamientoId);
}