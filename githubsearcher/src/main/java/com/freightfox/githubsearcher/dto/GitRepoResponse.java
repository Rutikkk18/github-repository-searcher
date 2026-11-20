package com.freightfox.githubsearcher.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitRepoResponse {


    private Long id;

    private String name;

    private String description;

    private String owner;

    private String language;

    private Integer stars;

    private Integer forks;

    private Instant lastUpdated;
}
