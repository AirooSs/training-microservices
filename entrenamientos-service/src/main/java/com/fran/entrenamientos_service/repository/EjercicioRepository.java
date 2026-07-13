package com.fran.entrenamientos_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fran.entrenamientos_service.model.Ejercicio;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long>{
	
	

}
