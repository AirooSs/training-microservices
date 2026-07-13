package com.fran.entrenamientos_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/* Encapsula toda la comunicacion HTTP hacia usuarios-service
 *  si el otro servicio cambia de URL o de forma de exponer el endpoint,
 *  solo hay que tocar esta clase, no cada sitio donde se usa*/

@Service
public class UsuarioClient {

	private final RestClient restClient;

	public UsuarioClient(@Value("${usuarios-service.url}") String usuariosServiceUrl) {
		this.restClient = RestClient.builder().baseUrl(usuariosServiceUrl).build();
	}

	/*
	 * Llama a GET /usuarios/{id}/existe en usuarios-service Si el servicio no
	 * responde (esta caido, timeout, etc.) lanzamos una excepcion propia
	 * (ServicioExternoNoDisponible) en vez de dejar que el error tecnico de HTTP
	 * suba tal cual hasta el controlador
	 */

	public boolean usuarioExiste(Long usuarioId) {
		try {
			Boolean existe = restClient.get().uri("/usuarios/{id}/existe", usuarioId).retrieve().body(Boolean.class);
			return Boolean.TRUE.equals(existe);
		} catch (RestClientException ex) {
			throw new ServicioExternoNoDisponibleException(
					"No se pudo comprobar el usuario en usuarios-service: " + ex.getMessage());
		}
	}
}