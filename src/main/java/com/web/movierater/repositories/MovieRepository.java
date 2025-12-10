package com.web.movierater.repositories;

import com.web.movierater.models.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {
    List<Movie> get();

    Movie getById(int id);

    Optional<Movie> getByTitle(String title);

    Movie create(Movie movie);

    Movie update(Movie movie);

    void delete(int movieId);
}
