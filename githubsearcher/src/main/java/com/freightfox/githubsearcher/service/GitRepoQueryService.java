
package com.freightfox.githubsearcher.service;

import com.freightfox.githubsearcher.dto.GitRepoResponse;
import com.freightfox.githubsearcher.entity.GitRepoEntity;
import com.freightfox.githubsearcher.repository.GitRepoRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitRepoQueryService {

    private final GitRepoRepository gitRepoRepository;

    public GitRepoQueryService(GitRepoRepository gitRepoRepository) {
        this.gitRepoRepository = gitRepoRepository;
    }

    @Transactional(readOnly = true)
    public List<GitRepoResponse> getRepositories(
            String language,
            Integer minStars,
            String sort) {

        // Build dynamic database filters
        Specification<GitRepoEntity> specification =
                (root, query, criteriaBuilder) -> {

                    List<Predicate> predicates = new ArrayList<>();

                    // Filter by programming language
                    if (language != null && !language.isBlank()) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        criteriaBuilder.lower(root.get("language")),
                                        language.trim().toLowerCase()
                                )
                        );
                    }

                    // Filter by minimum stars
                    if (minStars != null) {
                        predicates.add(
                                criteriaBuilder.greaterThanOrEqualTo(
                                        root.<Integer>get("stars"),
                                        minStars
                                )
                        );
                    }

                    return criteriaBuilder.and(
                            predicates.toArray(new Predicate[0])
                    );
                };

        // Determine sorting field
        String sortField = switch (
                sort == null || sort.isBlank()
                        ? "stars"
                        : sort.trim().toLowerCase()
                ) {
            case "stars" -> "stars";
            case "forks" -> "forks";
            case "updated" -> "lastUpdated";
            default -> throw new IllegalArgumentException(
                    "Invalid sort value. Use stars, forks, or updated."
            );
        };

        // Sort in descending order
        Sort sorting = Sort.by(
                Sort.Direction.DESC,
                sortField
        );

        // Fetch filtered and sorted repositories
        List<GitRepoEntity> entities =
                gitRepoRepository.findAll(specification, sorting);

        // Convert entities into response DTOs
        return entities.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private GitRepoResponse mapToResponse(GitRepoEntity entity) {

        return GitRepoResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .owner(entity.getOwner())
                .language(entity.getLanguage())
                .stars(entity.getStars())
                .forks(entity.getForks())
                .lastUpdated(entity.getLastUpdated())
                .build();
    }
}
