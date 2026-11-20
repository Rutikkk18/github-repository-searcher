
package com.freightfox.githubsearcher.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI githubSearcherOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("GitHub Repository Searcher API")
                        .description(
                                "REST API to search GitHub repositories, " +
                                        "store repository information, and retrieve " +
                                        "saved repositories with filters and sorting."
                        )
                        .version("1.0.0")
                );
    }
}
