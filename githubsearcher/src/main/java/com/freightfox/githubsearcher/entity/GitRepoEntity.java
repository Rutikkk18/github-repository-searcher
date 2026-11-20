package com.freightfox.githubsearcher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


import java.time.Instant;


@Table(name = "github_repositories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class GitRepoEntity {

    @Id
    private Long id;

    private  String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String owner;

    private String language;

    private Integer stars;

    private Integer forks;

    private Instant lastUpdated;

}


