
package com.freightfox.githubrepositorysearcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;


@Service
public class GithubService {

    private final GithubRepositoryRepository repository;
    private final RestClient restClient;

    public GithubService(GithubRepositoryRepository repository) {
        this.repository = repository;
        this.restClient = RestClient.builder()
        .baseUrl("https://api.github.com")
        .defaultHeader("Accept", "application/vnd.github+json")
        .defaultHeader("User-Agent", "GithubRepositorySearcher")
        .defaultHeader("Accept-Charset", "UTF-8")
        .build();
        
    }

    public List<GithubRepository> searchRepositories(
            String query, String language, String sort) {

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Search query must not be empty.");
        }

        String searchQuery = query.trim();

if (language != null && !language.isBlank()) {
    searchQuery += " language:" + language.trim();
}

final String finalSearchQuery = searchQuery;
        

        String requestedSort = (sort == null || sort.isBlank())
                ? "stars"
                : sort.trim().toLowerCase(Locale.ROOT);

        final String sortBy =
                List.of("stars", "forks", "help-wanted-issues", "updated")
                        .contains(requestedSort)
                        ? requestedSort
                        : "stars";
        
Map<?, ?> response;

try {
    response = restClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/search/repositories")
                    .queryParam("q", finalSearchQuery)
                    .queryParam("sort", sortBy)
                    .queryParam("per_page", 20)
                    .build())
            .retrieve()
            .body(Map.class);

} catch (HttpClientErrorException.TooManyRequests e) {
    throw new GithubApiException(
            "GitHub API rate limit exceeded. Please try again later.",
            429);

} catch (HttpClientErrorException.Unauthorized e) {
    throw new GithubApiException(
            "GitHub rejected the request. Please check API access.",
            401);

} catch (RestClientResponseException e) {
    throw new GithubApiException(
            "GitHub API returned an error. HTTP status: "
                    + e.getStatusCode().value(),
            e.getStatusCode().value());

} catch (RestClientException e) {
    throw new GithubApiException(
            "Unable to connect to GitHub. Please try again later.",
            503);
}

        

        List<GithubRepository> savedRepositories = new ArrayList<>();

        if (response == null
                || !(response.get("items") instanceof List<?> items)) {
            return savedRepositories;
        }

        for (Object item : items) {
            if (!(item instanceof Map<?, ?> data)) {
                continue;
            }

            Object idObject = data.get("id");

            if (!(idObject instanceof Number id)) {
                continue;
            }

            Long githubId = id.longValue();

            GithubRepository repo = repository.findById(githubId)
                    .orElseGet(GithubRepository::new);

            repo.setId(githubId);
            repo.setName((String) data.get("name"));
            
            System.out.println("Description received from GitHub: "
                    + data.get("description"));
            repo.setDescription((String) data.get("description"));

            

            Object ownerObject = data.get("owner");

            if (ownerObject instanceof Map<?, ?> ownerData) {
                repo.setOwner((String) ownerData.get("login"));
            }

            repo.setLanguage((String) data.get("language"));

            Number stars = (Number) data.get("stargazers_count");
            repo.setStars(stars == null ? 0 : stars.intValue());

            Number forks = (Number) data.get("forks_count");
            repo.setForks(forks == null ? 0 : forks.intValue());

            repo.setLastUpdated((String) data.get("updated_at"));

            savedRepositories.add(repository.save(repo));
        }

        return savedRepositories;
    }

    public List<GithubRepository> getAllRepositories(
            String language, Integer minStars, String sort) {

        Stream<GithubRepository> results = repository.findAll().stream();

        if (language != null && !language.isBlank()) {
            results = results.filter(repo ->
                    repo.getLanguage() != null
                    && repo.getLanguage()
                            .equalsIgnoreCase(language.trim()));
        }

        if (minStars != null) {
            results = results.filter(repo ->
                    repo.getStars() != null
                    && repo.getStars() >= minStars);
        }

        String sortBy = (sort == null || sort.isBlank())
                ? "stars"
                : sort.trim().toLowerCase(Locale.ROOT);

        Comparator<GithubRepository> comparator = switch (sortBy) {
            case "forks" -> Comparator.comparing(
                    GithubRepository::getForks,
                    Comparator.nullsFirst(Comparator.naturalOrder()));

            case "updated" -> Comparator.comparing(
                    GithubRepository::getLastUpdated,
                    Comparator.nullsFirst(Comparator.naturalOrder()));

            default -> Comparator.comparing(
                    GithubRepository::getStars,
                    Comparator.nullsFirst(Comparator.naturalOrder()));
        };

        return results.sorted(comparator.reversed()).toList();
    }
}
