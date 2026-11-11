package com.fran.entrenamientos_service.service;

import com.fran.entrenamientos_service.event.PrBatidoEvent;
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
    private final PrEventPublisher eventPublisher;

    @Autowired
    public PRService(PRRepository repo, EjercicioService ejercicioSrv, UsuarioClient usuarioClient,
                     PrEventPublisher eventPublisher) {
        this.repo = repo;
        this.ejercicioSrv = ejercicioSrv;
        this.usuarioClient = usuarioClient;
        this.eventPublisher = eventPublisher;
    }

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

        PR guardado = repo.save(nuevoPr);

        // Solo se publica despues de guardar: el evento anuncia algo que ya ocurrio
        eventPublisher.publicar(new PrBatidoEvent(
                guardado.getUsuarioId(),
                ejercicio.getId(),
                ejercicio.getNombre(),
                guardado.getPesoMaximo(),
                String.valueOf(guardado.getFecha())));

        return guardado;
    }

    public List<PR> historialDeUsuario(Long usuarioId) {
        if (!usuarioClient.usuarioExiste(usuarioId)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id " + usuarioId);
        }
        return repo.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }
}