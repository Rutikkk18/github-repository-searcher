package com.freightfox.githubsearcher.service;




import com.freightfox.githubsearcher.client.GitHubClient;
import com.freightfox.githubsearcher.dto.GitHubApiItem;
import com.freightfox.githubsearcher.dto.GitHubApiResponse;
import com.freightfox.githubsearcher.dto.GitRepoSearchRequest;
import com.freightfox.githubsearcher.dto.GitRepoSearchResponse;
import com.freightfox.githubsearcher.repository.GitRepoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitRepoSearchServiceTest {

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private GitRepoRepository gitRepoRepository;

    @InjectMocks
    private GitRepoSearchService gitRepoSearchService;


    @Test
    void shouldFetchAndSaveRepositoriesSuccessfully() {

        // Arrange: Prepare mock GitHub response

        GitHubApiItem item = new GitHubApiItem(
                123L,
                "spring-boot-example",
                "Spring Boot example project",
                "Java",
                450,
                120,
                Instant.parse("2024-01-01T12:00:00Z"),
                new GitHubApiItem.Owner("user123")
        );

        GitHubApiResponse apiResponse = new GitHubApiResponse(
                1,
                false,
                List.of(item)
        );

        GitRepoSearchRequest request = new GitRepoSearchRequest(
                "spring boot",
                "Java",
                "stars"
        );

        when(gitHubClient.searchRepositories(
                "spring boot",
                "Java",
                "stars"
        )).thenReturn(apiResponse);

        when(gitRepoRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // Act: Execute the service method

        GitRepoSearchResponse response =
                gitRepoSearchService.SearchRepositories(request);


        // Assert: Verify the response

        assertThat(response.getMessage())
                .isEqualTo("Repositories fetched and saved successfully");

        assertThat(response.getRepositories()).hasSize(1);

        assertThat(response.getRepositories().get(0).getId())
                .isEqualTo(123L);

        assertThat(response.getRepositories().get(0).getName())
                .isEqualTo("spring-boot-example");

        assertThat(response.getRepositories().get(0).getOwner())
                .isEqualTo("user123");

        assertThat(response.getRepositories().get(0).getStars())
                .isEqualTo(450);

        assertThat(response.getRepositories().get(0).getLastUpdated())
                .isEqualTo(Instant.parse("2024-01-01T12:00:00Z"));

        verify(gitHubClient).searchRepositories(
                "spring boot",
                "Java",
                "stars"
        );

        verify(gitRepoRepository).saveAll(anyList());
    }


    @Test
    void shouldReturnEmptyListWhenNoRepositoriesFound() {

        // Arrange

        GitRepoSearchRequest request = new GitRepoSearchRequest(
                "nonexistent-project",
                "Java",
                "stars"
        );

        GitHubApiResponse apiResponse = new GitHubApiResponse(
                0,
                false,
                List.of()
        );

        when(gitHubClient.searchRepositories(
                "nonexistent-project",
                "Java",
                "stars"
        )).thenReturn(apiResponse);


        // Act

        GitRepoSearchResponse response =
                gitRepoSearchService.SearchRepositories(request);


        // Assert

        assertThat(response.getMessage())
                .isEqualTo("No repositories found for your search");

        assertThat(response.getRepositories()).isEmpty();

        verify(gitHubClient).searchRepositories(
                "nonexistent-project",
                "Java",
                "stars"
        );

        verifyNoInteractions(gitRepoRepository);
    }
}
