package com.web.movierater.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.concurrent.CompletableFuture;

@Service
public class MovieEnrichmentServiceImpl
        implements MovieEnrichmentService {

    private static final String OMDB_ERROR_FORMAT = "OMDB call failed for %s: %s";

    private final MovieService movieService;
    private final WebClient webClient;
    private final String omdbApiKey;

    public MovieEnrichmentServiceImpl(MovieService movieService,
                                      WebClient.Builder webClientBuilder,
                                      @Value("${omdb.api.key}") String omdbApiKey) {
        this.movieService = movieService;
        this.webClient = webClientBuilder.baseUrl("http://www.omdbapi.com/").build();
        this.omdbApiKey = omdbApiKey;
    }

    @Override
    @Async("enrichmentExecutor")
    public CompletableFuture<Void> enrichRatingAsync(int id, String title) {
        return webClient.get()
                .uri(buildOmdbUri(title))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorResume(e -> {System.out.println(
                        String.format(OMDB_ERROR_FORMAT,
                                title, e.getMessage())
                        );
                    return Mono.empty();
                })
                .flatMap(json -> {
                    if (json.hasNonNull("imdbRating")) {
                        Double imdbRating = json.get("imdbRating").asDouble();
                        return Mono.fromRunnable(() ->
                            movieService.updateRating(id, imdbRating)
                        );
                    }
                    return Mono.empty();
                })
                .then()
                .toFuture();
    }

    private String buildOmdbUri (String title) {
        return UriComponentsBuilder.fromUriString("")
                .queryParam("t", title)
                .queryParam("apikey", omdbApiKey)
                .build()
                .toUriString();
    }
}
