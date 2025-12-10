package com.web.movierater.services;

import com.web.movierater.exceptions.AuthorizationException;
import com.web.movierater.exceptions.EntityNotFoundException;
import com.web.movierater.models.Movie;
import com.web.movierater.models.User;
import com.web.movierater.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }


    @Override
    public List<Movie> get() {
        return movieRepository.get();
    }

    @Override
    public Movie getById(int id) {
        return movieRepository.getById(id);
    }

    @Override
    public Movie getByTitle(String title) {
        if (movieRepository.getByTitle(title).isPresent()) {
            return movieRepository.getByTitle(title).get();
        }
        throw new EntityNotFoundException("Movie", "title", title);

    }

    @Override
    public Movie create(Movie movie, User requester) {
        checkPermissionsAdmin(requester);

        movie = movieRepository.create(movie);
        return movie;
    }

    @Override
    public Movie update(int id, Movie updatedMovie, User requester) {
        checkPermissionsAdmin(requester);

        Movie movieFromDb = movieRepository.getById(id);

        movieFromDb.setDirector(updatedMovie.getDirector());
        movieFromDb.setReleaseYear(updatedMovie.getReleaseYear());
        movieFromDb.setRating(updatedMovie.getRating());

        updatedMovie = movieRepository.update(movieFromDb);
        return updatedMovie;
    }

    @Override
    public void delete(int id, User requester) {
        checkPermissionsAdmin(requester);

        movieRepository.delete(id);
    }

    private void checkPermissionsAdmin (User requester) {
        if (!requester.isAdmin()) {
            throw new AuthorizationException();
        }
    }
}
