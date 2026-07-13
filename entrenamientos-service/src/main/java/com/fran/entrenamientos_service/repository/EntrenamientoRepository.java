package com.fran.entrenamientos_service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fran.entrenamientos_service.model.Entrenamiento;

public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Long> {
	
	// Util para un endpoint tipo "entrenamiento del dia"
	List<Entrenamiento> findByFecha(LocalDate fecha);

}
