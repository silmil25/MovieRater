package com.web.movierater.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieEnrichmentServiceImplTest {
    @Mock
    private MovieService movieService;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;
    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private MovieEnrichmentServiceImpl service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString()))
                .thenReturn(webClientBuilder);
        when(webClientBuilder.build())
                .thenReturn(webClient);

        service = new MovieEnrichmentServiceImpl(
                movieService,
                webClientBuilder,
                "test-api-key"
        );
    }

    @Test
    void enrichRatingAsync_should_updateRating_when_imdbRatingExists() throws Exception {
        // arrange
        JsonNode json = objectMapper.readTree("""
            {
              "Title": "Inception",
              "Year": "2010",
              "imdbRating": "8.8"
            }
        """);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.just(json));

        // act
        CompletableFuture<Void> future =
                service.enrichRatingAsync(1, "Inception");
        future.join();

        // assert
        verify(movieService).updateRating(1, 8.8);
        verifyNoMoreInteractions(movieService);
    }

    @Test
    void enrichRatingAsync_should_notUpdateRating_when_imdbRatingIsNull() throws Exception {
        // arrange
        JsonNode json = objectMapper.readTree("""
            {
              "Title": "Inception",
              "imdbRating": null
            }
        """);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.just(json));

        // act
        CompletableFuture<Void> future =
                service.enrichRatingAsync(1, "Inception");
        future.join();

        // assert
        verify(movieService, never()).updateRating(anyInt(), anyDouble());
    }

    @Test
    void enrichRatingAsync_should_notUpdateRating_when_imdbRatingIsMissing() throws Exception {
        // arrange
        JsonNode json = objectMapper.readTree("""
            {
              "Title": "Inception"
            }
        """);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.just(json));

        // act
        CompletableFuture<Void> future =
                service.enrichRatingAsync(1, "Inception");
        future.join();

        // assert
        verify(movieService, never()).updateRating(anyInt(), anyDouble());
    }

    @Test
    void enrichRatingAsync_should_completeNormally_when_omdbReturnsError() {
        // arrange
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.error(new RuntimeException("OMDB error")));

        // act
        CompletableFuture<Void> future =
                service.enrichRatingAsync(1, "Inception");

        // assert
        assertDoesNotThrow(future::join);
        verify(movieService, never()).updateRating(anyInt(), anyDouble());
    }
}
