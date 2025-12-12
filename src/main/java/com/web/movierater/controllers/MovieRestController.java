package com.web.movierater.controllers;

import com.web.movierater.helpers.AuthenticationHelper;
import com.web.movierater.helpers.ModelMapper;
import com.web.movierater.models.Movie;
import com.web.movierater.models.User;
import com.web.movierater.models.dtos.MovieDto;
import com.web.movierater.services.MovieEnrichmentService;
import com.web.movierater.services.MovieEnrichmentServiceImpl;
import com.web.movierater.services.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
public class MovieRestController {

    private static final String MOVIE_DELETED_MESSAGE = "Movie has been deleted with id: ";

    private final MovieService movieService;
    private final MovieEnrichmentService movieEnrichmentService;
    private final AuthenticationHelper authenticationHelper;
    private final ModelMapper modelMapper;

    @Autowired
    public MovieRestController(MovieService movieService,
                               MovieEnrichmentService movieEnrichmentService,
                               AuthenticationHelper authenticationHelper,
                               ModelMapper modelMapper) {
        this.movieService = movieService;
        this.movieEnrichmentService = movieEnrichmentService;
        this.authenticationHelper = authenticationHelper;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<?> get(@RequestHeader HttpHeaders headers) {

        authenticationHelper.tryGetUser(headers);
        List<MovieDto> movies = movieService.get().stream()
                .map(modelMapper::movieToDto)
                .toList();

        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id,
                                     @RequestHeader HttpHeaders headers) {
        authenticationHelper.tryGetUser(headers);

        MovieDto movie = modelMapper.movieToDto(movieService.getById(id));

        return ResponseEntity.ok(movie);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid MovieDto movieDto,
                                    @RequestHeader HttpHeaders headers) {
        User requester = authenticationHelper.tryGetUser(headers);

        Movie newMovie = modelMapper.dtoToMovie(movieDto);
        newMovie = movieService.create(newMovie, requester);

        movieEnrichmentService.enrichRatingAsync(newMovie.getId(), newMovie.getTitle());

        return ResponseEntity.ok(modelMapper.movieToDto(newMovie));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id,
                                    @Valid @RequestBody MovieDto movieDto,
                                    @RequestHeader HttpHeaders headers) {
        User requester = authenticationHelper.tryGetUser(headers);

        Movie updatedMovie = movieService.update(
                id, modelMapper.dtoToMovie(movieDto), requester);

        movieEnrichmentService.enrichRatingAsync(updatedMovie.getId(),
                updatedMovie.getTitle());

        return ResponseEntity.ok(modelMapper.movieToDto(updatedMovie));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable int id,
                                                 @RequestHeader HttpHeaders headers) {
        User requester = authenticationHelper.tryGetUser(headers);
        movieService.delete(id, requester);

        return ResponseEntity.ok(MOVIE_DELETED_MESSAGE + id);
    }
}
