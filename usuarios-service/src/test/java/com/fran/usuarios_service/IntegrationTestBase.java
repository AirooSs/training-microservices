package com.fran.usuarios_service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

// Clase base para tests de integracion
// Levanta un contenedor MySQL antes de todos los tests de las clases que la extiendan,
// y lo comparte entre ellas para no pagar el coste de arrancar un contenedor por cada clase

public abstract class IntegrationTestBase {

    static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8")
            .withDatabaseName("usuarios_test_db")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void iniciarContenedor() {
        mysqlContainer.start();
    }

    @AfterAll
    static void detenerContenedor() {
        mysqlContainer.stop();
    }

    @DynamicPropertySource
    static void propiedadesDinamicas(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }
}
