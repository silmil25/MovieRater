package com.web.movierater.controllers;

import com.web.movierater.helpers.ModelMapper;
import com.web.movierater.security.CustomUserDetails;
import com.web.movierater.models.Movie;
import com.web.movierater.models.User;
import com.web.movierater.models.dtos.MovieDto;
import com.web.movierater.services.MovieEnrichmentService;
import com.web.movierater.services.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieRestController {

    private static final String MOVIE_DELETED_MESSAGE = "Movie has been deleted with id: ";

    private final MovieService movieService;
    private final MovieEnrichmentService movieEnrichmentService;
    private final ModelMapper modelMapper;

    @Autowired
    public MovieRestController(MovieService movieService,
                               MovieEnrichmentService movieEnrichmentService,
                               ModelMapper modelMapper) {
        this.movieService = movieService;
        this.movieEnrichmentService = movieEnrichmentService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> get() {
        List<MovieDto> movies = movieService.get().stream()
                .map(modelMapper::movieToDto)
                .toList();

        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getById(@PathVariable int id) {
        MovieDto movie = modelMapper.movieToDto(movieService.getById(id));

        return ResponseEntity.ok(movie);
    }

    @Transactional
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody @Valid MovieDto movieDto,
                                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User requester = customUserDetails.getUser();

        Movie newMovie = modelMapper.dtoToMovie(movieDto);
        newMovie = movieService.create(newMovie, requester);

        movieEnrichmentService.enrichRatingAsync(newMovie.getId(), newMovie.getTitle());

        return ResponseEntity.ok(modelMapper.movieToDto(newMovie));
    }

    @Transactional
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable int id,
                                    @Valid @RequestBody MovieDto movieDto,
                                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User requester = customUserDetails.getUser();

        Movie updatedMovie = movieService.update(
                id, modelMapper.dtoToMovie(movieDto), requester);

        movieEnrichmentService.enrichRatingAsync(updatedMovie.getId(),
                updatedMovie.getTitle());

        return ResponseEntity.ok(modelMapper.movieToDto(updatedMovie));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUserById(@PathVariable int id,
                                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User requester = customUserDetails.getUser();

        movieService.delete(id, requester);

        return ResponseEntity.ok(MOVIE_DELETED_MESSAGE + id);
    }
}
