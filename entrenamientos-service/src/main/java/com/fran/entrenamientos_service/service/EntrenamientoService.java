package com.fran.entrenamientos_service.service;

import com.fran.entrenamientos_service.model.Entrenamiento;
import com.fran.entrenamientos_service.repository.EntrenamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EntrenamientoService {

	private final EntrenamientoRepository entrenamientoRepository;

	@Autowired
	public EntrenamientoService(EntrenamientoRepository entrenamientoRepository) {
		this.entrenamientoRepository = entrenamientoRepository;
	}

	public Entrenamiento crear(Entrenamiento entrenamiento) {
		return entrenamientoRepository.save(entrenamiento);
	}

	public Entrenamiento buscarPorId(Long id) {
		return entrenamientoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Entrenamiento no encontrado con id: " + id));
	}

	public List<Entrenamiento> listarTodos() {
		return entrenamientoRepository.findAll();
	}

	// Entrenamientos programados para hoy
	public List<Entrenamiento> deHoy() {
		return entrenamientoRepository.findByFecha(LocalDate.now());
	}
}