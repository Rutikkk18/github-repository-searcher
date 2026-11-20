
package com.freightfox.githubsearcher.exception;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler =
            new GlobalExceptionHandler();


    // Test 1: GitHub API error

    @Test
    void shouldHandleGitHubApiException() {

        // Arrange

        GitHubApiException exception = new GitHubApiException(
                "GitHub API error",
                HttpStatus.NOT_FOUND
        );


        // Act

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleGitHubApiException(exception);


        // Assert

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(response.getBody().getMessage())
                .isEqualTo("GitHub API error");

        assertThat(response.getBody().getStatus())
                .isEqualTo(404);
    }


    // Test 2: Rate limit exceeded

    @Test
    void shouldHandleRateLimitExceeded() {

        // Arrange

        RateLimitExceededException exception =
                new RateLimitExceededException(
                        "GitHub API rate limit exceeded",
                        HttpStatus.TOO_MANY_REQUESTS,
                        60L
                );


        // Act

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleRateLimitExceeded(exception);


        // Assert

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

        assertThat(response.getHeaders().getFirst("Retry-After"))
                .isEqualTo("60");

        assertThat(response.getBody().getMessage())
                .isEqualTo("GitHub API rate limit exceeded");
    }


    // Test 3: Invalid argument

    @Test
    void shouldHandleIllegalArgumentException() {

        // Arrange

        IllegalArgumentException exception =
                new IllegalArgumentException(
                        "Invalid sort value"
                );


        // Act

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleIllegalArgument(exception);


        // Assert

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response.getBody().getMessage())
                .isEqualTo("Invalid sort value");

        assertThat(response.getBody().getStatus())
                .isEqualTo(400);
    }


    // Test 4: Unexpected exception

    @Test
    void shouldHandleUnexpectedException() {

        // Arrange

        Exception exception =
                new RuntimeException("Database connection failed");


        // Act

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleUnexpectedException(exception);


        // Assert

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        assertThat(response.getBody().getMessage())
                .isEqualTo("An unexpected error occurred");

        assertThat(response.getBody().getStatus())
                .isEqualTo(500);
    }
}
