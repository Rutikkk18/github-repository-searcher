
package com.freightfox.githubsearcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GitHubClientConfig {

    @Value("${github.api.base-url}")
    private String baseUrl;

    @Value("${github.api.token:}")
    private String token;

    @Bean
    public RestClient githubRestClient() {

        RestClient.Builder clientBuilder = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("User-Agent", "GitHub-Repository-Searcher");

        if (token != null && !token.isBlank()) {
            clientBuilder.defaultHeader(
                    "Authorization",
                    "Bearer " + token
            );
        }

        return clientBuilder.build();
    }
}
