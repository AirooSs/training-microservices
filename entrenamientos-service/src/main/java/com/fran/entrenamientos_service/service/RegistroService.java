package com.fran.entrenamientos_service.service;

import com.fran.entrenamientos_service.model.Entrenamiento;
import com.fran.entrenamientos_service.model.Registro;
import com.fran.entrenamientos_service.repository.RegistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistroService {

	private final RegistroRepository repo;
	private final EntrenamientoService entrenoSrv;
	private final UsuarioClient usuarioClient;

	@Autowired
	public RegistroService(RegistroRepository repo, EntrenamientoService entrenoSrv,
			UsuarioClient usuarioClient) {
		this.repo = repo;
		this.entrenoSrv = entrenoSrv;
		this.usuarioClient = usuarioClient;
	}
	
	//CREAR REGISTRO
	public Registro crear(Long usuarioId, Long entrenoId, Registro datos) {
		// Primero comprobamos en usuarios-service que el usuario existe de verdad
		// esto es una llamada HTTP real entre los dos microservicios
		
		if (!usuarioClient.usuarioExiste(usuarioId)) {
			throw new RecursoNoEncontradoException("Usuario no encontrado con id " + usuarioId);
		}

		Entrenamiento entreno = entrenoSrv.buscarPorId(entrenoId);

		datos.setUsuarioId(usuarioId);
		datos.setEntrenamiento(entreno);

		return repo.save(datos);
	}

	// Historial de entrenamientos de un usuario
	public List<Registro> historialDeUsuario(Long usuarioId) {
		if (!usuarioClient.usuarioExiste(usuarioId)) {
			throw new RecursoNoEncontradoException("Usuario no encontrado con id " + usuarioId);
		}
		return repo.findByUsuarioIdOrderByFechaDesc(usuarioId);
	}
}