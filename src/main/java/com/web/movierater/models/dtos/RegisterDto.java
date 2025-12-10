package com.web.movierater.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterDto {
    private static final String USERNAME_EMPTY_ERROR = "Please enter a username.";
    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 30;
    private static final String USERNAME_LENGTH_ERROR = "Please provide a username between 5 and 30 characters.";

    private static final String PASSWORD_EMPTY_ERROR = "Please enter a password.";
    private static final int PASSWORD_MIN_LENGTH = 3;
    private static final int PASSWORD_MAX_LENGTH = 30;
    private static final String PASSWORD_LENGTH_ERROR = "Please provide a password between 5 and 30 characters.";

    @NotNull(message = USERNAME_EMPTY_ERROR)
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH,
            message = USERNAME_LENGTH_ERROR)
    private String username;

    @NotNull(message = PASSWORD_EMPTY_ERROR)
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH,
            message = PASSWORD_LENGTH_ERROR)
    private String password;

    @NotNull(message = PASSWORD_EMPTY_ERROR)
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH,
            message = PASSWORD_LENGTH_ERROR)
    private String repeatPassword;

    public RegisterDto () {}

    public RegisterDto (String username, String password, String repeatPassword) {
        this.setUsername(username);
        this.setPassword(password);
        setRepeatPassword(repeatPassword);
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }
    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
