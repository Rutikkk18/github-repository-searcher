
package com.freightfox.githubsearcher.controller;

import com.freightfox.githubsearcher.dto.GitRepoResponse;
import com.freightfox.githubsearcher.dto.GitRepoSearchRequest;
import com.freightfox.githubsearcher.dto.GitRepoSearchResponse;
import com.freightfox.githubsearcher.dto.GitRepoListResponse;
import com.freightfox.githubsearcher.service.GitRepoQueryService;
import com.freightfox.githubsearcher.service.GitRepoSearchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitRepoController {

    private final GitRepoSearchService gitRepoSearchService;
    private final GitRepoQueryService gitRepoQueryService;

    public GitRepoController(
            GitRepoQueryService gitRepoQueryService,
            GitRepoSearchService gitRepoSearchService) {

        this.gitRepoQueryService = gitRepoQueryService;
        this.gitRepoSearchService = gitRepoSearchService;
    }

    // Search repositories and save them in DB
    @PostMapping("/search")
    public ResponseEntity<GitRepoSearchResponse> searchRepositories(
            @Valid @RequestBody GitRepoSearchRequest request) {

        GitRepoSearchResponse response =
                gitRepoSearchService.SearchRepositories(request);

        return ResponseEntity.ok(response);
    }

    // Retrieve saved repositories with filters
    @GetMapping("/repositories")
    public ResponseEntity<GitRepoListResponse> getRepositories(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer minStars,
            @RequestParam(required = false) String sort) {

        List<GitRepoResponse> repositories =
                gitRepoQueryService.getRepositories(
                        language,
                        minStars,
                        sort
                );

        GitRepoListResponse response = GitRepoListResponse.builder()
                .repositories(repositories)
                .build();

        return ResponseEntity.ok(response);
    }
}
