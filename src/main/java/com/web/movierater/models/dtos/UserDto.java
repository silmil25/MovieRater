package com.web.movierater.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDto {
    private static final String USERNAME_EMPTY_ERROR = "Please enter a username.";
    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 30;
    private static final String USERNAME_LENGTH_ERROR = "Please provide a username between 5 and 30 characters.";

    @NotNull(message = USERNAME_EMPTY_ERROR)
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH,
            message = USERNAME_LENGTH_ERROR)

    private int id;

    private String username;

    private boolean isAdmin;

    public UserDto() {}

    public UserDto(String username, boolean isAdmin) {
        this.setUsername(username);
        this.setAdmin(isAdmin);
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
