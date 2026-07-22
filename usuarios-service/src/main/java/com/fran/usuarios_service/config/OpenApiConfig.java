package com.fran.usuarios_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI usuariosServiceOpenApi() {
		return new OpenAPI().info(new Info().title("Usuarios Service API")
				.description("Gestiona usuarios y su perfil fisico (peso, altura, historico). "
						+ "Expone tambien un endpoint interno usado por entrenamientos-service "
						+ "para validar la existencia de un usuario.")
				.version("1.0"));
	}
}