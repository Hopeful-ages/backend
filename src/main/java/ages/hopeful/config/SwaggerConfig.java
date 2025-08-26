package ages.hopeful.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Hopeful", 
        version = "1.0", 
        description = "Plataforma de Geração de cadastro e geração de Plano de Contingência para desastres"
    ),
    security = @SecurityRequirement(name = "bearerAuth"),
    tags = {
        @Tag(name = "Auth", description = "Endpoints de login e registro"),
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "Token JWT necessário para acessar os endpoints protegidos"
)
public class SwaggerConfig {
    
}