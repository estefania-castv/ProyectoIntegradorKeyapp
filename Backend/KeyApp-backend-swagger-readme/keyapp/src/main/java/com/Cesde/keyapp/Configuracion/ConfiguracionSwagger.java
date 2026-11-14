package com.Cesde.keyapp.Configuracion;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Información general de la documentación Swagger y botón "Authorize" para pegar el token JWT
// (primero se llama a POST /api/auth/login y se copia el campo "token").
@Configuration
public class ConfiguracionSwagger {

    @Bean
    public OpenAPI keyAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KeyApp API")
                        .description("Gestión de reservas y acceso a aulas mediante cerraduras inteligentes")
                        .version("0.0.1"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
