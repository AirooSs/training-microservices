package com.fran.usuarios_service.service;

import com.fran.usuarios_service.model.PerfilFisico;
import com.fran.usuarios_service.repository.PerfilFisicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerfilFisicoService {

	private final PerfilFisicoRepository perfilFisicoRepository;
	private final UsuarioService usuarioService;

	@Autowired
	public PerfilFisicoService(PerfilFisicoRepository perfilFisicoRepository, UsuarioService usuarioService) {
		this.perfilFisicoRepository = perfilFisicoRepository;
		this.usuarioService = usuarioService;
	}

	public PerfilFisico crear(PerfilFisico perfilFisico) {
		
		// Reutilizamos UsuarioService para comprobar que el usuario existe
		// antes de guardar el perfil, evitando perfiles huerfanos
		
		usuarioService.buscarPorId(perfilFisico.getUsuarioId());
		return perfilFisicoRepository.save(perfilFisico);
	}

	// Historial completo de un usuario, del mas reciente al mas antiguo
	
	public List<PerfilFisico> historialDeUsuario(Long usuarioId) {
		usuarioService.buscarPorId(usuarioId);
		return perfilFisicoRepository.findByUsuarioIdOrderByFechaRegistroDesc(usuarioId);
	}
}