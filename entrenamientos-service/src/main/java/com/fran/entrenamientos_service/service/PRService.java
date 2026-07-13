package com.fran.entrenamientos_service.service;

import com.fran.entrenamientos_service.model.Ejercicio;
import com.fran.entrenamientos_service.model.PR;
import com.fran.entrenamientos_service.repository.PRRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PRService {

    private final PRRepository repo;
    private final EjercicioService ejercicioSrv;
    private final UsuarioClient usuarioClient;

    @Autowired
    public PRService(PRRepository repo, EjercicioService ejercicioSrv, UsuarioClient usuarioClient) {
        this.repo = repo;
        this.ejercicioSrv = ejercicioSrv;
        this.usuarioClient = usuarioClient;
    }

    // Registra un nuevo peso levantado. Solo se guarda si supera el record anterior
    // del usuario en ese ejercicio, o si es el primero que registra
    
    public PR registrarIntento(Long usuarioId, Long ejercicioId, Double pesoLevantado) {
        if (!usuarioClient.usuarioExiste(usuarioId)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id " + usuarioId);
        }

        Ejercicio ejercicio = ejercicioSrv.buscarPorId(ejercicioId);

        Optional<PR> recordActual = repo.findTopByUsuarioIdAndEjercicioIdOrderByPesoMaximoDesc(usuarioId, ejercicioId);

        if (recordActual.isPresent() && pesoLevantado <= recordActual.get().getPesoMaximo()) {
            throw new IllegalArgumentException(
                    "El peso indicado no supera tu record actual de " + recordActual.get().getPesoMaximo() + " kg");
        }

        PR nuevoPr = new PR();
        nuevoPr.setUsuarioId(usuarioId);
        nuevoPr.setEjercicio(ejercicio);
        nuevoPr.setPesoMaximo(pesoLevantado);

        return repo.save(nuevoPr);
    }

    // Evolucion completa de PRs de un usuario
    public List<PR> historialDeUsuario(Long usuarioId) {
        if (!usuarioClient.usuarioExiste(usuarioId)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id " + usuarioId);
        }
        return repo.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }
}