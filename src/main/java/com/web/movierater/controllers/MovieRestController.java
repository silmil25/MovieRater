package com.web.movierater.controllers;

import com.web.movierater.helpers.AuthenticationHelper;
import com.web.movierater.helpers.ModelMapper;
import com.web.movierater.models.Movie;
import com.web.movierater.models.User;
import com.web.movierater.models.dtos.MovieDto;
import com.web.movierater.services.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/movies")
public class MovieRestController {

    private static final String MOVIE_DELETED_MESSAGE = "Movie has been deleted with id: ";

    private final MovieService movieService;
    private final AuthenticationHelper authenticationHelper;
    private final ModelMapper modelMapper;

    @Autowired
    public MovieRestController(MovieService movieService,
                               AuthenticationHelper authenticationHelper,
                               ModelMapper modelMapper) {
        this.movieService = movieService;
        this.authenticationHelper = authenticationHelper;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<?> get(@RequestHeader HttpHeaders headers) {

        try {
            authenticationHelper.tryGetUser(headers);
            List<MovieDto> movies = movieService.get().stream()
                    .map(modelMapper::movieToDto)
                    .toList();

            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id,
                                     @RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUser(headers);

            MovieDto movie = modelMapper.movieToDto(movieService.getById(id));

            return ResponseEntity.ok(movie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid MovieDto movieDto,
                                    BindingResult bindingResult,
                                    @RequestHeader HttpHeaders headers) {
        try {
            User requester = authenticationHelper.tryGetUser(headers);

            if (bindingResult.hasErrors()) {
                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField,
                                FieldError::getDefaultMessage));

                return ResponseEntity.badRequest().body(errors);
            }

            Movie newMovie = modelMapper.dtoToMovie(movieDto);
            MovieDto createdMovie = modelMapper.movieToDto(
                    movieService.create(newMovie, requester)
            );

            return ResponseEntity.ok(createdMovie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id,
                                    @Valid @RequestBody MovieDto movieDto,
                                    BindingResult bindingResult,
                                    @RequestHeader HttpHeaders headers) {
        try {
            User requester = authenticationHelper.tryGetUser(headers);

            if (bindingResult.hasErrors()) {
                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField,
                                FieldError::getDefaultMessage));

                return ResponseEntity.badRequest().body(errors);
            }

            MovieDto updatedMovie = modelMapper.movieToDto(movieService.update(
                    id, modelMapper.dtoToMovie(movieDto), requester));

            return ResponseEntity.ok(updatedMovie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable int id,
                                                 @RequestHeader HttpHeaders headers) {
        try {
            User requester = authenticationHelper.tryGetUser(headers);
            movieService.delete(id, requester);

            return ResponseEntity.ok(MOVIE_DELETED_MESSAGE + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }


}
