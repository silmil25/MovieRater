package com.web.movierater.helpers;

import com.web.movierater.models.Movie;
import com.web.movierater.models.dtos.MovieDto;
import com.web.movierater.models.dtos.RegisterDto;
import com.web.movierater.models.User;
import com.web.movierater.models.dtos.UserDto;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    // Users to and from DTOs

    public UserDto userToDto (User user) {
        UserDto userDto = new UserDto(user.getUsername(), user.isAdmin());
        userDto.setId(user.getId());
        return userDto;
    }

    public User registerDtoYoUser (RegisterDto register) {
        return new User(register.getUsername(), register.getPassword(), false);
    }

    public User userDtoToUser (UserDto userDto) {
        return new User(userDto.getUsername(), "", userDto.isAdmin());
    }

    // Movies to and from DTOs

    public MovieDto movieToDto (Movie movie) {
        MovieDto movieDto = new MovieDto(movie.getTitle(), movie.getDirector(),
                movie.getReleaseYear(), movie.getRating());
        movieDto.setId(movie.getId());
        return movieDto;
    }

    public Movie dtoToMovie(MovieDto movieDto) {
        return new Movie(movieDto.getTitle(), movieDto.getDirector(),
                movieDto.getReleaseYear(), movieDto.getRating());
    }
}
