package com.web.movierater.services;

import com.web.movierater.exceptions.AuthorizationException;
import com.web.movierater.exceptions.DuplicateEntityException;
import com.web.movierater.exceptions.EntityNotFoundException;
import com.web.movierater.models.User;
import com.web.movierater.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> get(User requester) {
        checkPermissionsAdmin(requester);
        return userRepository.get();
    }

    @Override
    public User getById(int id, User requester) {
        checkPermissionsAdminOrSameUser(id, requester);

        return userRepository.getById(id);
    }

    @Override
    public User getByUsername(String username) {
        if (userRepository.getByUsername(username).isPresent()) {
            return userRepository.getByUsername(username).get();
        }

        throw new EntityNotFoundException("User", "username", username);
    }

    @Override
    public User create(User user) {
        checkUniqueUsername(user.getUsername());

        user = userRepository.create(user);
        return user;
    }

    @Override
    public User update(int id, User updatedUser, User requester) {
        checkPermissionsAdmin(requester);

        User userFromDb = userRepository.getById(id);
        if (!updatedUser.getUsername().equals(userFromDb.getUsername())) {
            checkUniqueUsername(updatedUser.getUsername());
        }

        userFromDb.setUsername(updatedUser.getUsername());
        userFromDb.setAdmin(updatedUser.isAdmin());
        userRepository.update(userFromDb);

        return userFromDb;
    }

    @Override
    public void delete(int id, User requester) {
        checkPermissionsAdminOrSameUser(id, requester);

        userRepository.delete(id);
    }


    private void checkUniqueUsername(String username) {
        if (userRepository.getByUsername(username).isPresent()) {
            throw new DuplicateEntityException("User", "username", username);
        }
    }

    private void checkPermissionsAdmin (User requester) {
        if (!requester.isAdmin()) {
            throw new AuthorizationException();
        }
    }

    private void checkPermissionsAdminOrSameUser (int id, User requester) {
        if (!requester.isAdmin() && requester.getId() != id) {
            throw new AuthorizationException();
        }
    }
}
