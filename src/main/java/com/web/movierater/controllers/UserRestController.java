package com.web.movierater.controllers;

import com.web.movierater.helpers.AuthenticationHelper;
import com.web.movierater.helpers.ModelMapper;
import com.web.movierater.models.dtos.RegisterDto;
import com.web.movierater.models.User;
import com.web.movierater.models.dtos.UserDto;
import com.web.movierater.services.UserService;
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
@RequestMapping("/api/users")
public class UserRestController {

    private static final String PASSWORD_CONFIRMATION_SHOULD_MATCH_ERROR =
            "Password confirmation should match!";
    private static final String USER_DELETED_MESSAGE = "User has been deleted with id: ";

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final ModelMapper modelMapper;

    @Autowired
    public UserRestController(UserService userService,
                              AuthenticationHelper authenticationHelper,
                              ModelMapper modelMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<?> get(@RequestHeader HttpHeaders headers) {

        User requester = authenticationHelper.tryGetUser(headers);
        List<UserDto> users = userService.get(requester).stream()
                .map(modelMapper::userToDto)
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id,
                                     @RequestHeader HttpHeaders headers) {
        User requester = authenticationHelper.tryGetUser(headers);

        UserDto user = modelMapper.userToDto(userService.getById(id, requester));

        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid RegisterDto registerDto) {

        if (!registerDto.getPassword().equals(registerDto.getRepeatPassword())) {
            throw new IllegalArgumentException(PASSWORD_CONFIRMATION_SHOULD_MATCH_ERROR);
        }

        User newUser = modelMapper.registerDtoYoUser(registerDto);
        UserDto createdUser = modelMapper.userToDto(userService.create(newUser));

        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id,
                                    @Valid @RequestBody UserDto userDto,
                                    @RequestHeader HttpHeaders headers) {
        User requester = authenticationHelper.tryGetUser(headers);

        UserDto updatedUser = modelMapper.userToDto(userService.update(
                id, modelMapper.userDtoToUser(userDto), requester));

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable int id,
                                                 @RequestHeader HttpHeaders headers) {
        User requester = authenticationHelper.tryGetUser(headers);
        userService.delete(id, requester);

        return ResponseEntity.ok(USER_DELETED_MESSAGE + id);
    }
}
