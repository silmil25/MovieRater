package com.web.movierater.controllers;

import com.web.movierater.helpers.ModelMapper;
import com.web.movierater.security.CustomUserDetails;
import com.web.movierater.models.dtos.RegisterDto;
import com.web.movierater.models.User;
import com.web.movierater.models.dtos.UserDto;
import com.web.movierater.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Operations on users.")
public class UserRestController {

    private static final String PASSWORD_CONFIRMATION_SHOULD_MATCH_ERROR =
            "Password confirmation should match!";
    private static final String USER_DELETED_MESSAGE = "User has been deleted with id: ";

    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserRestController(UserService userService,
                              ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all users",
            description = "Operation only available for admins."
    )
    public ResponseEntity<?> get(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User requester = customUserDetails.getUser();
        List<UserDto> users = userService.get(requester).stream()
                .map(modelMapper::userToDto)
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Get user by ID",
            description = "Get a single user, by their id. If requester is admin, they can get details for any user, if the requester is not admin, they only have access to details for their own account."
    )
    public ResponseEntity<?> getById(@PathVariable int id,
                                     @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User requester = customUserDetails.getUser();

        UserDto user = modelMapper.userToDto(userService.getById(id, requester));

        return ResponseEntity.ok(user);
    }

    @PostMapping("/new")
    @Operation(
            summary = "Registering a new user",
            description = "Provide username, password and a matching repeat password to create a new user. USER role is assigned to all new users."
    )
    public ResponseEntity<?> create(@RequestBody @Valid RegisterDto registerDto) {

        if (!registerDto.getPassword().equals(registerDto.getRepeatPassword())) {
            throw new IllegalArgumentException(PASSWORD_CONFIRMATION_SHOULD_MATCH_ERROR);
        }

        User newUser = modelMapper.registerDtoYoUser(registerDto);
        UserDto createdUser = modelMapper.userToDto(userService.create(newUser));

        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Updating a user by ID",
            description = "Operation only available for admins. Used to grant or remove admin rights from other users."
    )
    public ResponseEntity<?> update(@PathVariable int id,
                                    @Valid @RequestBody UserDto userDto,
                                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User requester = customUserDetails.getUser();

        UserDto updatedUser = modelMapper.userToDto(userService.update(
                id, modelMapper.userDtoToUser(userDto), requester));

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Delete a user",
            description = "Delete a single user, by their id. If requester is admin, they can delete any user, if the requester is not admin, they only have permissions to delete their own account."
    )
    public ResponseEntity<String> deleteUserById(@PathVariable int id,
                                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User requester = customUserDetails.getUser();
        userService.delete(id, requester);

        return ResponseEntity.ok(USER_DELETED_MESSAGE + id);
    }
}
