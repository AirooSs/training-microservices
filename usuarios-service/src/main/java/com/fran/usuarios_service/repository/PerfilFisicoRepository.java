package com.fran.usuarios_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fran.usuarios_service.model.PerfilFisico;

public interface PerfilFisicoRepository extends JpaRepository<PerfilFisico, Long> {

	/*
	 * Historial completo de perfiles fisicos de un usuario, ordenado del mas
	 * reciente al mas antiguo
	 */

	List<PerfilFisico> findByUsuarioIdOrderByFechaRegistroDesc(Long usuarioId);
}
