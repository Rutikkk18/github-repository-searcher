
package com.freightfox.githubsearcher.service;

import com.freightfox.githubsearcher.dto.GitRepoResponse;
import com.freightfox.githubsearcher.entity.GitRepoEntity;
import com.freightfox.githubsearcher.repository.GitRepoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentMatchers;

@ExtendWith(MockitoExtension.class)
class GitRepoQueryServiceTest {

    @Mock
    private GitRepoRepository gitRepoRepository;

    @InjectMocks
    private GitRepoQueryService gitRepoQueryService;


    // Test 1: Default sorting by stars

    @Test
    void shouldReturnRepositoriesSortedByStarsByDefault() {

        // Arrange

        GitRepoEntity entity = createRepository();

        when(gitRepoRepository.findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Direction.DESC, "stars"))
        )).thenReturn(List.of(entity));


        // Act

        List<GitRepoResponse> response =
                gitRepoQueryService.getRepositories(null, null, null);


        // Assert

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName())
                .isEqualTo("spring-boot-example");

        assertThat(response.get(0).getStars()).isEqualTo(450);

        verify(gitRepoRepository).findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Direction.DESC, "stars"))
        );
    }


    // Test 2: Sorting by forks

    @Test
    void shouldSortRepositoriesByForks() {

        // Arrange

        GitRepoEntity entity = createRepository();

        when(gitRepoRepository.findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Direction.DESC, "forks"))
        )).thenReturn(List.of(entity));


        // Act

        List<GitRepoResponse> response =
                gitRepoQueryService.getRepositories(null, null, "forks");


        // Assert

        assertThat(response).hasSize(1);

        verify(gitRepoRepository).findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Direction.DESC, "forks"))
        );
    }


    // Test 3: Sorting by updated date

    @Test
    void shouldSortRepositoriesByUpdatedDate() {

        // Arrange

        GitRepoEntity entity = createRepository();

        when(gitRepoRepository.findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Direction.DESC, "lastUpdated"))
        )).thenReturn(List.of(entity));


        // Act

        List<GitRepoResponse> response =
                gitRepoQueryService.getRepositories(null, null, "updated");


        // Assert

        assertThat(response).hasSize(1);

        verify(gitRepoRepository).findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Direction.DESC, "lastUpdated"))
        );
    }


    // Test 4: Language and minimum stars filters

    @Test
    void shouldApplyLanguageAndMinimumStarsFilters() {

        // Arrange

        GitRepoEntity entity = createRepository();

        when(gitRepoRepository.findAll(
                any(Specification.class),
                any(Sort.class)
        )).thenReturn(List.of(entity));


        // Act

        List<GitRepoResponse> response =
                gitRepoQueryService.getRepositories("Java", 100, "stars");


        // Assert

        assertThat(response).hasSize(1);

        verify(gitRepoRepository).findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Direction.DESC, "stars"))
        );
    }


    // Test 5: Invalid sorting

    @Test
    void shouldThrowExceptionForInvalidSortValue() {

        // Act & Assert

        assertThatThrownBy(() ->
                gitRepoQueryService.getRepositories(
                        null,
                        null,
                        "invalid"
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid sort value");

        verifyNoInteractions(gitRepoRepository);
    }


    // Helper method to create test repository

    private GitRepoEntity createRepository() {

        return GitRepoEntity.builder()
                .id(123L)
                .name("spring-boot-example")
                .description("Spring Boot example project")
                .owner("user123")
                .language("Java")
                .stars(450)
                .forks(120)
                .lastUpdated(Instant.parse("2024-01-01T12:00:00Z"))
                .build();
    }
}
