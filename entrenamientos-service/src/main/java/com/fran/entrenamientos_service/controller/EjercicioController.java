package com.fran.entrenamientos_service.controller;

import com.fran.entrenamientos_service.model.Ejercicio;
import com.fran.entrenamientos_service.service.EjercicioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ejercicios")
public class EjercicioController {

	private final EjercicioService service;

	@Autowired
	public EjercicioController(EjercicioService service) {
		this.service = service;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Ejercicio> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(service.buscarPorId(id));
	}

	@GetMapping
	public ResponseEntity<List<Ejercicio>> listarTodos() {
		return ResponseEntity.ok(service.listarTodos());
	}

	@PostMapping
	public ResponseEntity<Ejercicio> crear(@Valid @RequestBody Ejercicio ejercicio) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(ejercicio));
	}

}