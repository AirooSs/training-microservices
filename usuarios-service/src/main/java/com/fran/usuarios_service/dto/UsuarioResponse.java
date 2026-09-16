package com.fran.usuarios_service.dto;

import com.fran.usuarios_service.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

// DTO de salida: representa el JSON que se devuelve al cliente
// Nunca incluye el password, ni siquiera hasheado
@Data
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String email;
    private LocalDate fechaNacimiento;
    private Usuario.Categoria categoria;
    private LocalDateTime fechaAlta;

    // Convierte una entidad Usuario en su DTO de respuesta, dejando fuera el password
    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getFechaNacimiento(),
                usuario.getCategoria(),
                usuario.getFechaAlta()
        );
    }
}
