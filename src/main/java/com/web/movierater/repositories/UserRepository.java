package com.web.movierater.repositories;

import com.web.movierater.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> get();

    User getById(int id);

    Optional<User> getByUsername(String username);

    User create(User user);

    User update(User user);

    void delete(int userId);
}
