
package com.freightfox.githubrepositorysearcher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubRepositoryRepository
        extends JpaRepository<GithubRepository, Long> {
}
