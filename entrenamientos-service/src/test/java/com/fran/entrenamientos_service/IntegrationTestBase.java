package com.fran.entrenamientos_service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

// Clase base para tests de integracion de entrenamientos-service
// Levanta un contenedor MySQL, igual que en usuarios-service

public abstract class IntegrationTestBase {

    static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8")
            .withDatabaseName("entrenamientos_test_db")
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