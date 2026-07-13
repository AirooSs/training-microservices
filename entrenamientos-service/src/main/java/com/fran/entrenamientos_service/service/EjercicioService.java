package com.fran.entrenamientos_service.service;

import com.fran.entrenamientos_service.model.Ejercicio;
import com.fran.entrenamientos_service.repository.EjercicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;

    @Autowired
    public EjercicioService(EjercicioRepository ejercicioRepository) {
        this.ejercicioRepository = ejercicioRepository;
    }
    
    public Ejercicio buscarPorId(Long id) {
        return ejercicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ejercicio no encontrado con id " + id));
    }

    public List<Ejercicio> listarTodos() {
        return ejercicioRepository.findAll();
    }

    public Ejercicio crear(Ejercicio ejercicio) {
        return ejercicioRepository.save(ejercicio);
    }

    
}