package com.fittlens.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Fittlens API", version = "v1"),
        security = @SecurityRequirement(name = "bearerAuth") // <<< Tell OpenAPI that JWT auth is needed
)
@SecurityScheme(
        name = "bearerAuth",                  // <<< Name must match SecurityRequirement
        type = SecuritySchemeType.HTTP,       // <<< Type = HTTP
        scheme = "bearer",                    // <<< Scheme = Bearer
        bearerFormat = "JWT"                  // <<< Format = JWT
)
public class OpenApiConfig {
}