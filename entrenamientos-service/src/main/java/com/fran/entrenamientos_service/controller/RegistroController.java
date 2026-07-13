package com.fran.entrenamientos_service.controller;

import com.fran.entrenamientos_service.dto.CrearRegistroRequest;
import com.fran.entrenamientos_service.model.Registro;
import com.fran.entrenamientos_service.service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registros")
public class RegistroController {

	private final RegistroService service;

	@Autowired
	public RegistroController(RegistroService service) {
		this.service = service;
	}

	@GetMapping("/usuario/{usuarioId}")
	public ResponseEntity<List<Registro>> historialDeUsuario(@PathVariable Long usuarioId) {
		return ResponseEntity.ok(service.historialDeUsuario(usuarioId));
	}

	@PostMapping
	public ResponseEntity<Registro> crear(@Valid @RequestBody CrearRegistroRequest req) {
		Registro datos = new Registro();
		datos.setSeries(req.getSeries());
		datos.setRepeticiones(req.getRepeticiones());
		datos.setPeso(req.getPeso());
		datos.setDuracionMinutos(req.getDuracionMinutos());

		Registro creado = service.crear(req.getUsuarioId(), req.getEntrenamientoId(), datos);
		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}

}