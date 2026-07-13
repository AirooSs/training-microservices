package com.fran.usuarios_service.controller;

import com.fran.usuarios_service.model.PerfilFisico;
import com.fran.usuarios_service.service.PerfilFisicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/perfiles")
public class PerfilFisicoController {

	private final PerfilFisicoService perfilFisicoService;

	@Autowired
	public PerfilFisicoController(PerfilFisicoService perfilFisicoService) {
		this.perfilFisicoService = perfilFisicoService;
	}
	// Crear perfil fisico

	@PostMapping
	public ResponseEntity<PerfilFisico> crear(@Valid @RequestBody PerfilFisico perfilFisico) {
		PerfilFisico creado = perfilFisicoService.crear(perfilFisico);
		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}

	// Historial completo de un usuario, para ver su evolucion de peso y altura

	@GetMapping("/usuario/{usuarioId}")
	public ResponseEntity<List<PerfilFisico>> historialDeUsuario(@PathVariable Long usuarioId) {
		return ResponseEntity.ok(perfilFisicoService.historialDeUsuario(usuarioId));
	}
}