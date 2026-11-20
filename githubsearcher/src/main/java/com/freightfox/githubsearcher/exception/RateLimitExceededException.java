
package com.freightfox.githubsearcher.exception;

import org.springframework.http.HttpStatus;

public class RateLimitExceededException extends GitHubApiException {

    private final Long retryAfterSeconds;

    public RateLimitExceededException(
            String message,
            HttpStatus status,
            Long retryAfterSeconds) {

        super(message, status);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public Long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
