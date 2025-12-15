package com.mentory.ense_proyect.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
            .info(new Info()
                .title("Mentory API")
                .version("1.0.0"))
            .addSecurityItem(new SecurityRequirement()
                .addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomizer addApiVersionHeaderGlobally() {
        return openApi -> {
            Parameter apiVersionHeader = new Parameter()
                    .in("header")
                    .name("API-Version")
                    .description("API version header. Default = 1.")
                    .schema(new StringSchema()._default("1"));

            openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    // Avoid duplicating if already present
                    boolean present = operation.getParameters() != null && operation.getParameters().stream()
                            .anyMatch(p -> "header".equalsIgnoreCase(p.getIn()) && "API-Version".equalsIgnoreCase(p.getName()));
                    if (!present) {
                        operation.addParametersItem(apiVersionHeader);
                    }
                })
            );
        };
    }
}
