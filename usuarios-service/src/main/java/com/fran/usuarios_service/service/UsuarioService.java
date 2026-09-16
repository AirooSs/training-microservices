package com.fran.usuarios_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fran.usuarios_service.model.Usuario;
import com.fran.usuarios_service.repository.UsuarioRepository;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Autowired
	public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public String login(String email, String password) {
		Usuario usuario = usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("Email o contrasena incorrectos"));

		if (!passwordEncoder.matches(password, usuario.getPassword())) {
			throw new IllegalArgumentException("Email o contrasena incorrectos");
		}

		return jwtService.generarToken(usuario.getId(), usuario.getEmail());
	}

	public Usuario crear(Usuario usuario) {
		if (usuarioRepository.existsByEmail(usuario.getEmail())) {
			throw new IllegalArgumentException("Ya existe un usuario con el mismo email");
		}
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		return usuarioRepository.save(usuario);
	}

	public Usuario buscarPorId(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
	}

	public List<Usuario> listarTodos() {
		return usuarioRepository.findAll();
	}

	public Usuario actualizar(Long id, Usuario datosActualizados) {
		Usuario usuarioExistente = buscarPorId(id);

		usuarioExistente.setNombre(datosActualizados.getNombre());
		usuarioExistente.setEmail(datosActualizados.getEmail());
		usuarioExistente.setFechaNacimiento(datosActualizados.getFechaNacimiento());
		usuarioExistente.setCategoria(datosActualizados.getCategoria());

		return usuarioRepository.save(usuarioExistente);
	}

	public void eliminar(Long id) {
		// Comprobamos que existe antes de borrar, si no existiera lanzaria la excepcion
		// aqui mismo
		buscarPorId(id);
		usuarioRepository.deleteById(id);
	}

	// Este metodo lo usara entrenamientos-service via HTTP para validar que un
	// usuario existe
	// antes de dejarle crear un registro o un PR
	public boolean existe(Long id) {
		return usuarioRepository.existsById(id);
	}

}
