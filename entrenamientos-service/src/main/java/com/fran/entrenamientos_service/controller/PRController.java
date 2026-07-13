package com.fran.entrenamientos_service.controller;

import com.fran.entrenamientos_service.dto.RegistrarPrRequest;
import com.fran.entrenamientos_service.model.PR;
import com.fran.entrenamientos_service.service.PRService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prs")
public class PRController {

	private final PRService service;

	@Autowired
	public PRController(PRService service) {
		this.service = service;
	}

	@GetMapping("/usuario/{usuarioId}")
	public ResponseEntity<List<PR>> historialDeUsuario(@PathVariable Long usuarioId) {
		return ResponseEntity.ok(service.historialDeUsuario(usuarioId));
	}

	@PostMapping
	public ResponseEntity<PR> registrarIntento(@Valid @RequestBody RegistrarPrRequest req) {
		PR creado = service.registrarIntento(req.getUsuarioId(), req.getEjercicioId(), req.getPesoLevantado());
		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}

}