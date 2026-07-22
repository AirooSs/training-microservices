package com.fran.entrenamientos_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI entrenamientosServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Entrenamientos Service API")
                        .description("Gestiona ejercicios, entrenamientos, registros y records personales (PR). "
                                + "Valida la existencia del usuario contra usuarios-service via HTTP "
                                + "antes de crear un registro o un PR.")
                        .version("1.0"));
    }
}