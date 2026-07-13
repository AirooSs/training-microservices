package com.fran.entrenamientos_service.controller;

import com.fran.entrenamientos_service.model.Entrenamiento;
import com.fran.entrenamientos_service.service.EntrenamientoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entrenamientos")
public class EntrenamientoController {

	private final EntrenamientoService service;

	@Autowired
	public EntrenamientoController(EntrenamientoService service) {
		this.service = service;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Entrenamiento> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(service.buscarPorId(id));
	}

	@GetMapping
	public ResponseEntity<List<Entrenamiento>> listarTodos() {
		return ResponseEntity.ok(service.listarTodos());
	}

	@GetMapping("/hoy")
	public ResponseEntity<List<Entrenamiento>> deHoy() {
		return ResponseEntity.ok(service.deHoy());
	}

	@PostMapping
	public ResponseEntity<Entrenamiento> crear(@Valid @RequestBody Entrenamiento entrenamiento) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(entrenamiento));
	}

}
