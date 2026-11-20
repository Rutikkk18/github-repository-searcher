package com.freightfox.githubsearcher.repository;

import com.freightfox.githubsearcher.entity.GitRepoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GitRepoRepository extends JpaRepository<GitRepoEntity, Long> ,
        JpaSpecificationExecutor<GitRepoEntity> {
}
