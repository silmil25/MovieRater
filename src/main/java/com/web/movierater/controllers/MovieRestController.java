package com.web.movierater.controllers;

import com.web.movierater.helpers.ModelMapper;
import com.web.movierater.security.CustomUserDetails;
import com.web.movierater.models.Movie;
import com.web.movierater.models.User;
import com.web.movierater.models.dtos.MovieDto;
import com.web.movierater.services.MovieEnrichmentService;
import com.web.movierater.services.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Movie Controller", description = "Operations on movies.")
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
    @Operation(
            summary = "Get all movies",
            description = "If a search term is provided, get all movies with the search term in the title instead"
    )
    public ResponseEntity<?> get(
            @RequestParam(value = "search", required = false) String searchTitle) {
        List<MovieDto> movies = movieService.get(searchTitle).stream()
                .map(modelMapper::movieToDto)
                .toList();

        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Get movie by ID",
            description = "Get a single movie, by its id"
    )
    public ResponseEntity<?> getById(@PathVariable int id) {
        MovieDto movie = modelMapper.movieToDto(movieService.getById(id));

        return ResponseEntity.ok(movie);
    }

    @Transactional
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a movie",
            description = "Operation only available for admins. Provide a title, and optional director and year of the movie, to create a new movie. The rating will be fetched in the background."
    )
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
    @Operation(
            summary = "Update a movie by ID",
            description = "Operation only available for admins. Update the director and/or year of the movie. The rating will also be updated in the background."
    )
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
    @Operation(
            summary = "Delete a movie",
            description = "Operation only available for admins. Provide a movie ID to delete the movie from the database."
    )
    public ResponseEntity<String> deleteUserById(@PathVariable int id,
                                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User requester = customUserDetails.getUser();

        movieService.delete(id, requester);

        return ResponseEntity.ok(MOVIE_DELETED_MESSAGE + id);
    }
}
