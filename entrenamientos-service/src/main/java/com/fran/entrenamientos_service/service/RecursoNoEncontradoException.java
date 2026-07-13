package com.fran.entrenamientos_service.service;

// Excepcion simple para cuando se busca un recurso que no existe
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}