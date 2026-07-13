package com.fran.entrenamientos_service.controller;

import com.fran.entrenamientos_service.dto.AgregarEjercicioRequest;
import com.fran.entrenamientos_service.model.EntrenamientoEjercicio;
import com.fran.entrenamientos_service.service.EntrenamientoEjercicioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entrenamiento-ejercicios")
public class EntrenamientoEjercicioController {

	private final EntrenamientoEjercicioService service;

	@Autowired
	public EntrenamientoEjercicioController(EntrenamientoEjercicioService service) {
		this.service = service;
	}

	@GetMapping("/entrenamiento/{entrenamientoId}")
	public ResponseEntity<List<EntrenamientoEjercicio>> listarPorEntrenamiento(@PathVariable Long entrenamientoId) {
		return ResponseEntity.ok(service.listarPorEntrenamiento(entrenamientoId));
	}

	@PostMapping
	public ResponseEntity<EntrenamientoEjercicio> agregarEjercicio(@Valid @RequestBody AgregarEjercicioRequest req) {
		EntrenamientoEjercicio creado = service.agregarEjercicio(req.getEntrenamientoId(), req.getEjercicioId(),
				req.getRepeticiones(), req.getPeso(), req.getOrden());
		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}

}