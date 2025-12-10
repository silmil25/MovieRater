package com.web.movierater.helpers;

import com.web.movierater.exceptions.AuthenticationException;
import com.web.movierater.exceptions.EntityNotFoundException;
import com.web.movierater.models.User;
import com.web.movierater.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationHelper {
    private static final String LOGIN_REQUIRED_ERROR = "This resource requires a login.";
    private static final String USERNAME_AND_PASSWORD_REQUIRED_ERROR = "Please provide both username and password.";
    private static final String INCORRECT_CREDENTIALS = "Incorrect username or password.";

    private final UserRepository userRepository;

    @Autowired
    public AuthenticationHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsHeader("Authorization")) {
            throw new AuthenticationException(LOGIN_REQUIRED_ERROR);
        }

        String[] credentials = headers.getFirst("Authorization")
                .split(" ");

        if (credentials.length < 2) {
            throw new AuthenticationException(USERNAME_AND_PASSWORD_REQUIRED_ERROR);
        }

        String username = credentials[0];
        String password = credentials[1];

        return authenticateUser(username, password);
    }

    public User authenticateUser(String username, String password) {
        Optional<User> user = userRepository.getByUsername(username);

        if (user.isEmpty() || !user.get().getPassword().equals(password)) {
            throw new AuthenticationException(INCORRECT_CREDENTIALS);

        }

        return user.get();
    }

}
