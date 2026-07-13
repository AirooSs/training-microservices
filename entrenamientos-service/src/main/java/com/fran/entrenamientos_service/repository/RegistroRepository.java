package com.fran.entrenamientos_service.repository;

import com.fran.entrenamientos_service.model.Registro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroRepository extends JpaRepository<Registro, Long> {

	// Historial de entrenamientos de un usuario
	List<Registro> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
}