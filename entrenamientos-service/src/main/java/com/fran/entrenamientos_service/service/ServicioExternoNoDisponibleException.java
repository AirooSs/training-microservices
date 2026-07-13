package com.fran.entrenamientos_service.service;

/* Se lanza cuando usuarios-service no responde (caido, timeout, error de red) 
 * la separamos de RecursoNoEncontradoException porque el significado es distinto:
 * aqui no sabemos si el usuario existe o no, simplemente no hemos podido preguntarlo*/

public class ServicioExternoNoDisponibleException extends RuntimeException {

	public ServicioExternoNoDisponibleException(String mensaje) {
		super(mensaje);
	}
}