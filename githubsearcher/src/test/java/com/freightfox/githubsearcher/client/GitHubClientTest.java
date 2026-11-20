
package com.freightfox.githubsearcher.client;

import com.freightfox.githubsearcher.dto.GitHubApiResponse;
import com.freightfox.githubsearcher.exception.GitHubApiException;
import com.freightfox.githubsearcher.exception.RateLimitExceededException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import static org.hamcrest.Matchers.containsString;

class GitHubClientTest {

    private GitHubClient gitHubClient;

    private MockRestServiceServer mockServer;


    @BeforeEach
    void setUp() {

        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://api.github.com");

        mockServer = MockRestServiceServer.bindTo(builder).build();

        RestClient restClient = builder.build();

        gitHubClient = new GitHubClient(restClient);
    }


    // Test 1: Successful GitHub API response

    @Test
    void shouldReturnRepositoriesSuccessfully() {

        // Arrange

        String jsonResponse = """
                {
                    "total_count": 1,
                    "incomplete_results": false,
                    "items": [
                        {
                            "id": 123,
                            "name": "spring-boot-example",
                            "description": "Spring Boot example",
                            "language": "Java",
                            "stargazers_count": 450,
                            "forks_count": 120,
                            "updated_at": "2024-01-01T12:00:00Z",
                            "owner": {
                                "login": "user123"
                            }
                        }
                    ]
                }
                """;

        mockServer.expect(requestTo(
                        containsString("/search/repositories")
                ))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        jsonResponse,
                        MediaType.APPLICATION_JSON
                ));


        // Act

        GitHubApiResponse response =
                gitHubClient.searchRepositories(
                        "spring boot",
                        "Java",
                        "stars"
                );


        // Assert

        assertThat(response.getTotalCount()).isEqualTo(1);

        assertThat(response.getItems()).hasSize(1);

        assertThat(response.getItems().get(0).getName())
                .isEqualTo("spring-boot-example");

        assertThat(response.getItems().get(0).getStars())
                .isEqualTo(450);

        assertThat(response.getItems().get(0).getOwner().getLogin())
                .isEqualTo("user123");

        mockServer.verify();
    }


    // Test 2: GitHub rate limit exceeded

    @Test
    void shouldThrowRateLimitExceptionWhenLimitExceeded() {

        // Arrange

        mockServer.expect(requestTo(
                        containsString("/search/repositories")
                ))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .header("X-RateLimit-Remaining", "0")
                        .header("Retry-After", "60")
                        .body("""
                                {"message": "API rate limit exceeded"}
                                """));


        // Act & Assert

        assertThatThrownBy(() ->
                gitHubClient.searchRepositories(
                        "spring boot",
                        "Java",
                        "stars"
                )
        )
                .isInstanceOf(RateLimitExceededException.class)
                .hasMessageContaining("rate limit exceeded")
                .satisfies(exception -> {
                    RateLimitExceededException rateLimitException =
                            (RateLimitExceededException) exception;

                    assertThat(rateLimitException.getRetryAfterSeconds())
                            .isEqualTo(60L);
                });

        mockServer.verify();
    }


    // Test 3: Other GitHub API error

    @Test
    void shouldThrowGitHubApiExceptionForOtherErrors() {

        // Arrange

        mockServer.expect(requestTo(
                        containsString("/search/repositories")
                ))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("""
                                {"message": "Not Found"}
                                """));


        // Act & Assert

        assertThatThrownBy(() ->
                gitHubClient.searchRepositories(
                        "spring boot",
                        "Java",
                        "stars"
                )
        )
                .isInstanceOf(GitHubApiException.class)
                .hasMessageContaining("GitHub API error")
                .satisfies(exception -> {
                    GitHubApiException apiException =
                            (GitHubApiException) exception;

                    assertThat(apiException.getStatus())
                            .isEqualTo(HttpStatus.NOT_FOUND);
                });

        mockServer.verify();
    }
}
