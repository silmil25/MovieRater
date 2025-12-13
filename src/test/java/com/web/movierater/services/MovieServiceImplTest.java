package com.web.movierater.services;

import com.web.movierater.exceptions.AuthorizationException;
import com.web.movierater.exceptions.EntityNotFoundException;
import com.web.movierater.models.Movie;
import com.web.movierater.models.User;
import com.web.movierater.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    private User adminUser;
    private User regularUser;
    private Movie sampleMovie;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setAdmin(true);

        regularUser = new User();
        regularUser.setAdmin(false);

        sampleMovie = new Movie();
        sampleMovie.setId(1);
        sampleMovie.setTitle("Inception");
        sampleMovie.setDirector("Christopher Nolan");
        sampleMovie.setReleaseYear(2010);
        sampleMovie.setRating(8.8);
    }

    // ----------------------- GET METHODS -----------------------

    @Test
    void get_should_returnListOfMovies_when_called() {
        // arrange
        when(movieRepository.get("Inception")).thenReturn(List.of(sampleMovie));

        // act
        List<Movie> result = movieService.get("Inception");

        // assert
        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void getById_should_returnMovie_when_movieExists() {
        // arrange
        when(movieRepository.getById(1)).thenReturn(sampleMovie);

        // act
        Movie result = movieService.getById(1);

        // assert
        assertEquals(sampleMovie, result);
    }

    @Test
    void getByTitle_should_returnMovie_when_movieExists() {
        // arrange
        when(movieRepository.getByTitle("Inception")).thenReturn(Optional.of(sampleMovie));

        // act
        Movie result = movieService.getByTitle("Inception");

        // assert
        assertEquals(sampleMovie, result);
    }

    @Test
    void getByTitle_should_throwEntityNotFoundException_when_movieDoesNotExist() {
        // arrange
        when(movieRepository.getByTitle("Nonexistent")).thenReturn(Optional.empty());

        // act & assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> movieService.getByTitle("Nonexistent")
        );

        assertTrue(exception.getMessage().contains("Movie"));
        assertTrue(exception.getMessage().contains("title"));
        assertTrue(exception.getMessage().contains("Nonexistent"));
    }

    // ----------------------- CREATE METHOD -----------------------

    @Test
    void create_should_saveMovie_when_userIsAdmin() {
        // arrange
        when(movieRepository.create(sampleMovie)).thenReturn(sampleMovie);

        // act
        Movie result = movieService.create(sampleMovie, adminUser);

        // assert
        assertEquals(sampleMovie, result);
        verify(movieRepository).create(sampleMovie);
    }

    @Test
    void create_should_throwAuthorizationException_when_userIsNotAdmin() {
        // arrange done in setUp

        // act & assert
        assertThrows(AuthorizationException.class,
                () -> movieService.create(sampleMovie, regularUser));

        verify(movieRepository, never()).create(any());
    }

    // ----------------------- UPDATE METHOD -----------------------

    @Test
    void update_should_updateMovie_when_userIsAdmin() {
        // arrange
        Movie updatedMovie = new Movie();
        updatedMovie.setDirector("Nolan");
        updatedMovie.setReleaseYear(2012);

        when(movieRepository.getById(1)).thenReturn(sampleMovie);
        when(movieRepository.update(any(Movie.class))).thenAnswer(inv -> inv.getArgument(0));

        // act
        Movie result = movieService.update(1, updatedMovie, adminUser);

        // assert
        assertEquals("Nolan", result.getDirector());
        assertEquals(2012, result.getReleaseYear());
        verify(movieRepository).update(sampleMovie);
    }

    @Test
    void update_should_throwAuthorizationException_when_userIsNotAdmin() {
        // act & assert
        assertThrows(AuthorizationException.class,
                () -> movieService.update(1, sampleMovie, regularUser));

        verify(movieRepository, never()).update(any());
    }

    // ----------------------- UPDATE RATING -----------------------

    @Test
    void updateRating_should_updateMovieRating_when_called() {
        // arrange
        when(movieRepository.getById(1)).thenReturn(sampleMovie);

        // act
        movieService.updateRating(1, 9.2);

        // assert
        assertEquals(9.2, sampleMovie.getRating());
        verify(movieRepository).update(sampleMovie);
    }

    // ----------------------- DELETE -----------------------

    @Test
    void delete_should_deleteMovie_when_userIsAdmin() {
        // arrange
        doNothing().when(movieRepository).delete(1);

        // act
        movieService.delete(1, adminUser);

        // assert
        verify(movieRepository).delete(1);
    }

    @Test
    void delete_should_throwAuthorizationException_when_userIsNotAdmin() {
        // act & assert
        assertThrows(AuthorizationException.class,
                () -> movieService.delete(1, regularUser));

        verify(movieRepository, never()).delete(anyInt());
    }

}
