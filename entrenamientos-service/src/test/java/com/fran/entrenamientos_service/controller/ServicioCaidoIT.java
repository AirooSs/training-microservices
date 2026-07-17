package com.fran.entrenamientos_service.controller;

import com.fran.entrenamientos_service.IntegrationTestBase;
import com.fran.entrenamientos_service.dto.CrearRegistroRequest;
import com.fran.entrenamientos_service.model.Entrenamiento;
import com.fran.entrenamientos_service.service.EntrenamientoService;
import okhttp3.mockwebserver.MockWebServer;
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

// Test aislado en su propia clase porque necesita fijar, desde el arranque del contexto,
// una URL de usuarios-service que apunte a un puerto donde no hay nada escuchando

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServicioCaidoIT extends IntegrationTestBase {

    // Puerto que se reserva, se libera inmediatamente y no vuelve a usarse en el test,
    // asi garantizamos que no hay nada escuchando ahi cuando se hace la peticion real
    static int puertoSinServicio;

    @LocalServerPort
    private int puerto;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntrenamientoService entrenamientoService;

    @BeforeAll
    static void reservarPuertoLibre() throws IOException {
        MockWebServer servidorTemporal = new MockWebServer();
        servidorTemporal.start();
        puertoSinServicio = servidorTemporal.getPort();
        servidorTemporal.shutdown();
    }

    @DynamicPropertySource
    static void apuntarAUnPuertoSinServicio(DynamicPropertyRegistry registry) {
        registry.add("usuarios-service.url", () -> "http://localhost:" + puertoSinServicio);
    }

    @Test
    void deberiaDevolver503CuandoUsuariosServiceNoResponde() {
        Entrenamiento entrenamiento = new Entrenamiento();
        entrenamiento.setNombre("Entrenamiento de prueba");
        entrenamiento.setTipoEntrenamiento(Entrenamiento.TipoEntrenamiento.FUERZA);
        entrenamiento.setDuracionMinutos(60);
        entrenamiento.setFecha(LocalDate.now());
        Entrenamiento guardado = entrenamientoService.crear(entrenamiento);

        CrearRegistroRequest peticion = new CrearRegistroRequest();
        peticion.setUsuarioId(1L);
        peticion.setEntrenamientoId(guardado.getId());

        ResponseEntity<String> respuesta = restTemplate.postForEntity(
                "http://localhost:" + puerto + "/registros", peticion, String.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
