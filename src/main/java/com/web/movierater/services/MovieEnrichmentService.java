package com.web.movierater.services;

import java.util.concurrent.CompletableFuture;

public interface MovieEnrichmentService {

    CompletableFuture<Void> enrichRatingAsync(int id, String title);
}
