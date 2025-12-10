package com.web.movierater.services;

import com.web.movierater.models.User;

import java.util.List;

public interface UserService {

    List<User> get(User requester);

    User getById(int id, User requester);

    User getByUsername(String username);

    User create(User user);

    User update(int id, User updatedUser, User requester);

    void delete(int id, User requester);
}
