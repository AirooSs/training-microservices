package com.fran.usuarios_service.controller;

import com.fran.usuarios_service.IntegrationTestBase;
import com.fran.usuarios_service.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// SpringBootTest con puerto aleatorio real: levanta la aplicacion completa
// (controlador, servicio, repositorio, base de datos del contenedor) para probar
// el flujo de extremo a extremo, no solo una clase aislada

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsuarioControllerIT extends IntegrationTestBase {
	
	@LocalServerPort
    private int puerto;

    @Autowired
    private TestRestTemplate restTemplate;

    private String urlBase() {
        return "http://localhost:" + puerto + "/usuarios";
    }

    @Test
    void deberiaCrearUsuarioCorrectamente() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Ana Torres");
        nuevoUsuario.setEmail("ana.torres@test.com");
        nuevoUsuario.setFechaNacimiento(LocalDate.of(1995, 3, 15));
        nuevoUsuario.setCategoria(Usuario.Categoria.INTERMEDIO);

        ResponseEntity<Usuario> respuesta = restTemplate.postForEntity(urlBase(), nuevoUsuario, Usuario.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getId()).isNotNull();
        assertThat(respuesta.getBody().getEmail()).isEqualTo("ana.torres@test.com");
        assertThat(respuesta.getBody().getFechaAlta()).isNotNull();
    }

    @Test
    void deberiaRechazarEmailDuplicado() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Carlos Ruiz");
        usuario.setEmail("carlos.ruiz@test.com");
        usuario.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        usuario.setCategoria(Usuario.Categoria.AVANZADO);

        // Primera creacion: deberia funcionar sin problema
        restTemplate.postForEntity(urlBase(), usuario, Usuario.class);

        // Segunda creacion con el mismo email: deberia rechazarse
        ResponseEntity<String> segundaRespuesta = restTemplate.postForEntity(urlBase(), usuario, String.class);

        assertThat(segundaRespuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(segundaRespuesta.getBody()).contains("Ya existe un usuario con el mismo email");
    }

    @Test
    void deberiaDevolver404SiElUsuarioNoExiste() {
        ResponseEntity<String> respuesta = restTemplate.getForEntity(urlBase() + "/9999", String.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}