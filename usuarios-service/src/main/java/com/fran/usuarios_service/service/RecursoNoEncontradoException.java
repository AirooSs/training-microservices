package com.fran.usuarios_service.service;

// Excepcion simple para cuando se busca un recurso que no existe
// La usamos en los servicios y la capturamos mas adelante en un manejador global de errores

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}