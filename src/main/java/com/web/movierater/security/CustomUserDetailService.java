package com.web.movierater.security;

import com.web.movierater.models.User;
import com.web.movierater.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private static final String INCORRECT_CREDENTIALS = "Incorrect username or password.";

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Optional<User> user = userRepository
                .getByUsername(username);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException(INCORRECT_CREDENTIALS);
        }

        return new CustomUserDetails(user.get());
    }
}
