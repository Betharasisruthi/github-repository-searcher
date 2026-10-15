
package com.freightfox.githubrepositorysearcher;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github")
public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

@PostMapping(value = "/search", produces = "application/json;charset=UTF-8")


public List<GithubRepository> searchRepositories(@RequestBody SearchRequest request) {
    try {
        return githubService.searchRepositories(
                request.getQuery(),
                request.getLanguage(),
                request.getSort());

    } catch (IllegalArgumentException e) {
        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                e.getMessage());

    } catch (GithubApiException e) {
        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatusCode.valueOf(e.getStatusCode()),
                e.getMessage());
    }
}

    

    

    @GetMapping("/repositories")
public List<GithubRepository> getRepositories(
        @RequestParam(required = false) String language,
        @RequestParam(required = false) Integer minStars,
        @RequestParam(defaultValue = "stars") String sort) {

    return githubService.getAllRepositories(language, minStars, sort);
}
   

    public static class SearchRequest {
        private String query;
        private String language;
        private String sort;

        public SearchRequest() {
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }
    }
}
