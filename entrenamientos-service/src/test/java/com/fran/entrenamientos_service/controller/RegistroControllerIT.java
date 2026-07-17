package com.fran.entrenamientos_service.controller;

import com.fran.entrenamientos_service.IntegrationTestBase;
import com.fran.entrenamientos_service.dto.CrearRegistroRequest;
import com.fran.entrenamientos_service.model.Entrenamiento;
import com.fran.entrenamientos_service.service.EntrenamientoService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/* MockWebServer simula usuarios-service durante el test, sin necesitar el servicio real arrancado
 asi el test es autocontenido y reproducible en cualquier maquina (o en un pipeline de CI)
 cubre los dos casos en los que usuarios-service SI responde: usuario existente y usuario inexistente
 el caso de usuarios-service caido se prueba aparte, en ServicioCaidoIT,
 porque requiere fijar una URL distinta desde el arranque del contexto de Spring */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegistroControllerIT extends IntegrationTestBase {

	static MockWebServer usuariosServiceSimulado;

	@LocalServerPort
	private int puerto;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private EntrenamientoService entrenamientoService;

	@BeforeAll
	static void iniciarServidorSimulado() throws IOException {
		usuariosServiceSimulado = new MockWebServer();
		usuariosServiceSimulado.start();
	}

	@AfterAll
	static void detenerServidorSimulado() throws IOException {
		usuariosServiceSimulado.shutdown();
	}

	// Sobreescribe la URL de usuarios-service para que apunte al servidor simulado,
	// en vez de al localhost:8081 real definido en application.properties
	
	@DynamicPropertySource
	static void redirigirUsuariosService(DynamicPropertyRegistry registry) {
		registry.add("usuarios-service.url", () -> "http://localhost:" + usuariosServiceSimulado.getPort());
	}

	private String urlBase() {
		return "http://localhost:" + puerto + "/registros";
	}

	@Test
	void deberiaCrearRegistroCuandoElUsuarioExiste() {

		// Preparamos la respuesta que dara el usuarios-service simulado: "true", el
		// usuario existe
		usuariosServiceSimulado
				.enqueue(new MockResponse().setBody("true").addHeader("Content-Type", "application/json"));

		Entrenamiento entrenamiento = crearEntrenamientoDePrueba();

		CrearRegistroRequest peticion = new CrearRegistroRequest();
		peticion.setUsuarioId(1L);
		peticion.setEntrenamientoId(entrenamiento.getId());
		peticion.setSeries(4);
		peticion.setRepeticiones(8);
		peticion.setPeso(80.0);

		ResponseEntity<String> respuesta = restTemplate.postForEntity(urlBase(), peticion, String.class);

		assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(respuesta.getBody()).contains("\"usuarioId\":1");
	}

	@Test
	void deberiaRechazarRegistroCuandoElUsuarioNoExiste() {
		// Esta vez el usuarios-service simulado responde "false"
		usuariosServiceSimulado
				.enqueue(new MockResponse().setBody("false").addHeader("Content-Type", "application/json"));

		Entrenamiento entrenamiento = crearEntrenamientoDePrueba();

		CrearRegistroRequest peticion = new CrearRegistroRequest();
		peticion.setUsuarioId(999L);
		peticion.setEntrenamientoId(entrenamiento.getId());
		peticion.setSeries(3);
		peticion.setRepeticiones(10);
		peticion.setPeso(50.0);

		ResponseEntity<String> respuesta = restTemplate.postForEntity(urlBase(), peticion, String.class);

		assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	private Entrenamiento crearEntrenamientoDePrueba() {
		Entrenamiento entrenamiento = new Entrenamiento();
		entrenamiento.setNombre("Entrenamiento de prueba");
		entrenamiento.setTipoEntrenamiento(Entrenamiento.TipoEntrenamiento.FUERZA);
		entrenamiento.setDuracionMinutos(60);
		entrenamiento.setFecha(LocalDate.now());
		return entrenamientoService.crear(entrenamiento);
	}
}
