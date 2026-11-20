package com.freightfox.githubsearcher.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitRepoSearchResponse {


    private String message;

    private List<GitRepoResponse> repositories;
}
