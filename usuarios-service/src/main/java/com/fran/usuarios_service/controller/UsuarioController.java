package com.fran.usuarios_service.controller;

import com.fran.usuarios_service.model.Usuario;
import com.fran.usuarios_service.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;

	@Autowired
	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	// Buscar por id
	@GetMapping("/{id}")
	public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(usuarioService.buscarPorId(id));
	}

	// Buscar todos
	@GetMapping
	public ResponseEntity<List<Usuario>> listarTodos() {
		return ResponseEntity.ok(usuarioService.listarTodos());
	}

	// Crear usuario
	@PostMapping
	public ResponseEntity<Usuario> crear(@Valid @RequestBody Usuario usuario) {
		Usuario creado = usuarioService.crear(usuario);
		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}

	// Actualizar usuario
	@PutMapping("/{id}")
	public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
		return ResponseEntity.ok(usuarioService.actualizar(id, usuario));
	}

	// Eliminar usuario
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		usuarioService.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	// Endpoint interno que usara entrenamientos-service para comprobar
	// si un usuario existe antes de crear un registro o un peso maximo
	
	@GetMapping("/{id}/existe")
	public ResponseEntity<Boolean> existe(@PathVariable Long id) {
		return ResponseEntity.ok(usuarioService.existe(id));
	}
}