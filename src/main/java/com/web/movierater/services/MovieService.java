package com.web.movierater.services;

import com.web.movierater.models.Movie;
import com.web.movierater.models.User;

import java.util.List;

public interface MovieService {
    List<Movie> get();

    Movie getById(int id);

    Movie getByTitle(String title);

    Movie create(Movie movie, User requester);

    Movie update(int id, Movie updatedMovie, User requester);

    void delete(int id, User requester);
}
