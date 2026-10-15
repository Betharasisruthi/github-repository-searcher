
package com.freightfox.githubrepositorysearcher;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class GithubRepositorySearcherApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void emptySearchQueryShouldBeRejected() {
        GithubService service = new GithubService(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.searchRepositories("", "Java", "stars")
        );
    }
}
