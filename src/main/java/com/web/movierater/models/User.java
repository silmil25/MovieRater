package com.web.movierater.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    private static final String USERNAME_EMPTY_ERROR = "Please enter a username.";
    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 30;
    private static final String USERNAME_LENGTH_ERROR = "Please provide a username between 5 and 30 characters.";

    private static final String PASSWORD_EMPTY_ERROR = "Please enter a username.";
    private static final int PASSWORD_MIN_LENGTH = 3;
    private static final int PASSWORD_MAX_LENGTH = 30;
    private static final String PASSWORD_LENGTH_ERROR = "Please provide a username between 5 and 30 characters.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "is_admin")
    private boolean isAdmin;

    public User() {}

    public User(String username, String password, boolean isAdmin) {
        this.setUsername(username);
        this.setPassword(password);
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

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
