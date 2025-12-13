package com.web.movierater.security;

import com.web.movierater.models.User;
import com.web.movierater.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("john");
        user.setPassword("password");
    }

    @Test
    void loadUserByUsername_should_returnUserDetails_when_userExists() {
        // arrange
        when(userRepository.getByUsername("john"))
                .thenReturn(Optional.of(user));

        // act
        UserDetails result = customUserDetailService
                .loadUserByUsername("john");

        // assert
        assertNotNull(result);
        assertEquals("john", result.getUsername());
        assertTrue(result instanceof CustomUserDetails);

        verify(userRepository).getByUsername("john");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void loadUserByUsername_should_throwUsernameNotFoundException_when_userDoesNotExist() {
        // arrange
        when(userRepository.getByUsername("john"))
                .thenReturn(Optional.empty());

        // act + assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailService.loadUserByUsername("john")
        );

        assertEquals(
                "Incorrect username or password.",
                exception.getMessage()
        );

        verify(userRepository).getByUsername("john");
        verifyNoMoreInteractions(userRepository);
    }
}
