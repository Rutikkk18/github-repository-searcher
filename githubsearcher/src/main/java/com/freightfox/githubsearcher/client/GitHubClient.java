
package com.freightfox.githubsearcher.client;

import com.freightfox.githubsearcher.dto.GitHubApiResponse;
import com.freightfox.githubsearcher.exception.GitHubApiException;
import com.freightfox.githubsearcher.exception.RateLimitExceededException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class GitHubClient {

    private final RestClient restClient;

    public GitHubClient(RestClient githubRestClient) {
        this.restClient = githubRestClient;
    }

    public GitHubApiResponse searchRepositories(
            String query,
            String language,
            String sort) {

        final String searchQuery = (language != null && !language.isBlank())
                ? query + " language:" + language
                : query;

        GitHubApiResponse response = restClient.get()
                .uri(uriBuilder -> {
                    var uri = uriBuilder
                            .path("/search/repositories")
                            .queryParam("q", searchQuery)
                            .queryParam("order", "desc")
                            .queryParam("per_page", 30);

                    if (sort != null && !sort.isBlank()) {
                        uri.queryParam("sort", sort);
                    }

                    return uri.build();
                })
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        (request, apiResponse) -> {

                            HttpStatusCode status = apiResponse.getStatusCode();
                            HttpHeaders headers = apiResponse.getHeaders();

                            String errorBody = new String(
                                    apiResponse.getBody().readAllBytes(),
                                    StandardCharsets.UTF_8
                            );

                            String message = errorBody.isBlank()
                                    ? "GitHub API request failed"
                                    : errorBody;

                            boolean rateLimitExceeded =
                                    status.value() == 429 ||
                                            (status.value() == 403 &&
                                                    ("0".equals(headers.getFirst("X-RateLimit-Remaining"))
                                                            || headers.getFirst("Retry-After") != null));

                            if (rateLimitExceeded) {

                                Long retryAfter = getRetryAfterSeconds(headers);

                                throw new RateLimitExceededException(
                                        "GitHub API rate limit exceeded",
                                        HttpStatus.TOO_MANY_REQUESTS,
                                        retryAfter
                                );
                            }

                            HttpStatus httpStatus = HttpStatus.resolve(status.value());

                            if (httpStatus == null) {
                                httpStatus = HttpStatus.BAD_GATEWAY;
                            }

                            throw new GitHubApiException(
                                    "GitHub API error: " + message,
                                    httpStatus
                            );
                        }
                )
                .body(GitHubApiResponse.class);

        if (response == null) {
            throw new GitHubApiException(
                    "GitHub API returned an empty response",
                    HttpStatus.BAD_GATEWAY
            );
        }

        return response;
    }

    private Long getRetryAfterSeconds(HttpHeaders headers) {

        String retryAfter = headers.getFirst("Retry-After");

        if (retryAfter != null) {
            try {
                return Long.parseLong(retryAfter);
            } catch (NumberFormatException ignored) {
                // Fall back to GitHub's rate-limit reset timestamp.
            }
        }

        String resetTime = headers.getFirst("X-RateLimit-Reset");

        if (resetTime != null) {
            try {
                long seconds = Long.parseLong(resetTime)
                        - Instant.now().getEpochSecond();

                return Math.max(seconds, 0);
            } catch (NumberFormatException ignored) {
                // Reset timestamp was invalid or unavailable.
            }
        }

        return null;
    }
}
