package com.fran.entrenamientos_service.service;

import com.fran.entrenamientos_service.model.Ejercicio;
import com.fran.entrenamientos_service.model.Entrenamiento;
import com.fran.entrenamientos_service.model.EntrenamientoEjercicio;
import com.fran.entrenamientos_service.repository.EntrenamientoEjercicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntrenamientoEjercicioService {

	private final EntrenamientoEjercicioRepository repo;
	private final EntrenamientoService entrenoSrv;
	private final EjercicioService ejercicioSrv;

	@Autowired
	public EntrenamientoEjercicioService(EntrenamientoEjercicioRepository repo,
			EntrenamientoService entrenoSrv,EjercicioService ejercicioSrv) {
				this.repo = repo;
				this.entrenoSrv = entrenoSrv;
				this.ejercicioSrv = ejercicioSrv;
	}

	// Recibe solo los ids desde el controlador, y aqui resolvemos las entidades
	// completas
	// asegurandonos de que ambas existen antes de guardar la relacion
	public EntrenamientoEjercicio agregarEjercicio(Long entrenoId,
			Long ejercicioId, Integer reps, Double peso,Integer orden) {
		Entrenamiento entreno = entrenoSrv.buscarPorId(entrenoId);
		Ejercicio ejercicio = ejercicioSrv.buscarPorId(ejercicioId);

		EntrenamientoEjercicio relacion = new EntrenamientoEjercicio();
		relacion.setEntrenamiento(entreno);
		relacion.setEjercicio(ejercicio);
		relacion.setRepeticiones(reps);
		relacion.setPeso(peso);
		relacion.setOrden(orden);

		return repo.save(relacion);
	}

	// Todos los ejercicios que componen un entrenamiento, para mostrar el detalle
	// completo
	public List<EntrenamientoEjercicio> listarPorEntrenamiento(Long entrenoId) {
		entrenoSrv.buscarPorId(entrenoId);
		return repo.findByEntrenamientoId(entrenoId);
	}
}