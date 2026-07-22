package com.fran.entrenamientos_service.controller;

import com.fran.entrenamientos_service.dto.CrearRegistroRequest;
import com.fran.entrenamientos_service.model.Registro;
import com.fran.entrenamientos_service.service.RegistroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registros")
@Tag(name = "Registros", description = "Registro de resultados de entrenamientos de un usuario")
public class RegistroController {

    private final RegistroService service;

    @Autowired
    public RegistroController(RegistroService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un registro de entrenamiento",
            description = "Antes de guardar el registro, valida via HTTP contra usuarios-service "
                    + "que el usuarioId indicado existe.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro creado correctamente"),
            @ApiResponse(responseCode = "404", description = "El usuario o el entrenamiento indicado no existen"),
            @ApiResponse(responseCode = "503", description = "usuarios-service no esta disponible, "
                    + "no se pudo validar el usuario")
    })
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

    @Operation(summary = "Historial de entrenamientos de un usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Registro>> historialDeUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.historialDeUsuario(usuarioId));
    }
}