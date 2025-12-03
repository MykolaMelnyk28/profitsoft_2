package com.melnyk.profitsoft_2.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Book API",
        description = "REST API for book data management",
        version = "1.0.0"
    )
)
public class OpenAPIConfig {}
