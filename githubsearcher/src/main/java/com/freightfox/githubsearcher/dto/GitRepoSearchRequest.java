
package com.freightfox.githubsearcher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GitRepoSearchRequest {

    @NotBlank(message = "Search query is required")
    private String query;

    private String language;

    @Pattern(
            regexp = "^(stars|forks|updated)?$",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Sort must be stars, forks, or updated"
    )
    private String sort;
}
