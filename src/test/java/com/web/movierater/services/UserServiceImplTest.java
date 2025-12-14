package com.web.movierater.services;

import com.web.movierater.exceptions.AuthorizationException;
import com.web.movierater.exceptions.DuplicateEntityException;
import com.web.movierater.exceptions.EntityNotFoundException;
import com.web.movierater.models.User;
import com.web.movierater.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User adminUser;
    private User regularUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1);
        adminUser.setUsername("admin");
        adminUser.setAdmin(true);
        adminUser.setPassword("adminpass");

        regularUser = new User();
        regularUser.setId(2);
        regularUser.setUsername("user");
        regularUser.setAdmin(false);
        regularUser.setPassword("userpass");

        anotherUser = new User();
        anotherUser.setId(3);
        anotherUser.setUsername("another");
        anotherUser.setAdmin(false);
        anotherUser.setPassword("anotherpass");
    }

    // ----------------------- GET -----------------------

    @Test
    void get_should_returnAllUsers_when_requesterIsAdmin() {
        // arrange
        when(userRepository.get()).thenReturn(List.of(adminUser, regularUser));

        // act
        List<User> result = userService.get(adminUser);

        // assert
        assertEquals(2, result.size());
    }

    @Test
    void get_should_throwAuthorizationException_when_requesterIsNotAdmin() {
        // act & assert
        assertThrows(AuthorizationException.class, () -> userService.get(regularUser));
    }

    // ----------------------- GET BY ID -----------------------

    @Test
    void getById_should_returnUser_when_requesterIsAdmin() {
        // arrange
        when(userRepository.getById(2)).thenReturn(regularUser);

        // act
        User result = userService.getById(2, adminUser);

        // assert
        assertEquals(regularUser, result);
    }

    @Test
    void getById_should_returnUser_when_requesterIsSameUser() {
        // arrange
        when(userRepository.getById(2)).thenReturn(regularUser);

        // act
        User result = userService.getById(2, regularUser);

        // assert
        assertEquals(regularUser, result);
    }

    @Test
    void getById_should_throwAuthorizationException_when_requesterIsNotAdminOrSameUser() {
        // act & assert
        assertThrows(AuthorizationException.class, () -> userService.getById(2, anotherUser));
    }

    // ----------------------- GET BY USERNAME -----------------------

    @Test
    void getByUsername_should_returnUser_when_userExists() {
        // arrange
        when(userRepository.getByUsername("user")).thenReturn(Optional.of(regularUser));

        // act
        User result = userService.getByUsername("user");

        // assert
        assertEquals(regularUser, result);
    }

    @Test
    void getByUsername_should_throwEntityNotFoundException_when_userDoesNotExist() {
        // arrange
        when(userRepository.getByUsername("nonexistent")).thenReturn(Optional.empty());

        // act & assert
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getByUsername("nonexistent")
        );

        assertTrue(ex.getMessage().contains("User"));
        assertTrue(ex.getMessage().contains("username"));
        assertTrue(ex.getMessage().contains("nonexistent"));
    }

    // ----------------------- CREATE -----------------------

    @Test
    void create_should_hashPasswordAndSaveUser_when_usernameIsUnique() {
        // arrange
        when(userRepository.getByUsername("newuser")).thenReturn(Optional.empty());
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("plainpass");

        when(encoder.encode("plainpass")).thenReturn("hashedpass");
        when(userRepository.create(any(User.class))).thenAnswer(
                inv -> inv.getArgument(0));

        // act
        User result = userService.create(newUser);

        // assert
        assertEquals("hashedpass", result.getPassword());
        verify(userRepository).create(result);
    }

    @Test
    void create_should_throwDuplicateEntityException_when_usernameAlreadyExists() {
        // arrange
        when(userRepository.getByUsername("user")).thenReturn(Optional.of(regularUser));
        User duplicateUser = new User();
        duplicateUser.setUsername("user");

        // act & assert
        assertThrows(DuplicateEntityException.class, () -> userService.create(duplicateUser));
        verify(userRepository, never()).create(any());
    }

    // ----------------------- UPDATE -----------------------

    @Test
    void update_should_updateUser_when_requesterIsAdminAndUsernameIsUnique() {
        // arrange
        User updatedUser = new User();
        updatedUser.setUsername("updated");
        updatedUser.setAdmin(false);

        when(userRepository.getById(2)).thenReturn(regularUser);
        when(userRepository.getByUsername("updated")).thenReturn(Optional.empty());

        // act
        User result = userService.update(2, updatedUser, adminUser);

        // assert
        assertEquals("updated", result.getUsername());
        assertFalse(result.isAdmin());
        verify(userRepository).update(regularUser);
    }

    @Test
    void update_should_throwAuthorizationException_when_requesterIsNotAdmin() {
        // act & assert
        assertThrows(AuthorizationException.class, () -> userService.update(
                2, regularUser, regularUser));
        verify(userRepository, never()).update(any());
    }

    @Test
    void update_should_throwDuplicateEntityException_when_newUsernameAlreadyExists() {
        // arrange
        User updatedUser = new User();
        updatedUser.setUsername("existing");

        when(userRepository.getById(2)).thenReturn(regularUser);
        when(userRepository.getByUsername("existing")).thenReturn(Optional.of(anotherUser));

        // act & assert
        assertThrows(DuplicateEntityException.class, () -> userService.update(2, updatedUser, adminUser));
        verify(userRepository, never()).update(any());
    }

    // ----------------------- DELETE -----------------------

    @Test
    void delete_should_deleteUser_when_requesterIsAdmin() {
        // arrange
        doNothing().when(userRepository).delete(2);

        // act
        userService.delete(2, adminUser);

        // assert
        verify(userRepository).delete(2);
    }

    @Test
    void delete_should_deleteUser_when_requesterIsSameUser() {
        // arrange
        doNothing().when(userRepository).delete(2);

        // act
        userService.delete(2, regularUser);

        // assert
        verify(userRepository).delete(2);
    }

    @Test
    void delete_should_throwAuthorizationException_when_requesterIsNotAdminOrSameUser() {
        // act & assert
        assertThrows(AuthorizationException.class, () -> userService.delete(2, anotherUser));
        verify(userRepository, never()).delete(anyInt());
    }

}
