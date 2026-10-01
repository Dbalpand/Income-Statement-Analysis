package com.divyesh.incomestatementanalysis.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI api() {

        return new OpenAPI()

                .info(

                        new Info()

                                .title("Income Statement Analysis API")

                                .version("1.0")

                                .description("OCR + AWS Bedrock API")

                )

                .externalDocs(

                        new ExternalDocumentation()

                                .description("Documentation")

                );

    }

}