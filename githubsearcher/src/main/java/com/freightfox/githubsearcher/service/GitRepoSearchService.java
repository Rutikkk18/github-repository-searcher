package com.freightfox.githubsearcher.service;


import com.freightfox.githubsearcher.client.GitHubClient;
import com.freightfox.githubsearcher.dto.*;
import com.freightfox.githubsearcher.entity.GitRepoEntity;
import com.freightfox.githubsearcher.repository.GitRepoRepository;
import jakarta.validation.constraints.Null;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitRepoSearchService {

    private GitHubClient gitHubClient;

    private GitRepoRepository gitRepoRepository;

    public GitRepoSearchService(GitHubClient gitHubClient,
                                GitRepoRepository gitRepoRepository) {

        this.gitHubClient = gitHubClient;
        this.gitRepoRepository = gitRepoRepository;
    }


    public GitRepoSearchResponse SearchRepositories(
            GitRepoSearchRequest request) {

        // to fetch repo from github api
        GitHubApiResponse apiResponse =
                gitHubClient.searchRepositories(
                        request.getQuery(),
                        request.getLanguage(),
                        request.getSort()
                );

        //for empty search the results and msg handled here

        if(apiResponse.getItems() == null || apiResponse.getItems().isEmpty()){
            return GitRepoSearchResponse.builder()
                    .message("No repositories found for your search")
                    .repositories(List.of())
                    .build();
        }


        // now convert github  api items into database entities
        List<GitRepoEntity> entities =apiResponse.getItems()
                .stream()
                .map(this::mapToEntity)
                .toList();

        // save or up repo in database
        List<GitRepoEntity> savedEntities = gitRepoRepository.saveAll(entities);

        // saved entities to response sto

        List<GitRepoResponse> repositories = savedEntities
                .stream()
                .map(this::mapToResponse)
                .toList();

        // Final Response
        return GitRepoSearchResponse.builder()
                .message("Repositories fetched and saved successfully")
                .repositories(repositories)
                .build();
    }

    private GitRepoEntity mapToEntity(GitHubApiItem item){

        return GitRepoEntity.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner() !=null ? item.getOwner().getLogin(): null)
                .language(item.getLanguage())
                .stars(item.getStars())
                .forks(item.getForks())
                .lastUpdated(item.getUpdatedAt())
                .build();
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
