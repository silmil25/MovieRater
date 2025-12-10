package com.web.movierater.repositories;

import com.web.movierater.exceptions.EntityNotFoundException;
import com.web.movierater.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryDbImpl implements UserRepository {

    public static final String GET_ALL_USERS =
            "FROM User";

    public static final String GET_USER_BY_USERNAME =
            "from User where username = :username";


    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryDbImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public List<User> get() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query =session.createQuery(GET_ALL_USERS, User.class);
            return query.list();
        }
    }

    @Override
    public User getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                throw new EntityNotFoundException("User", id);
            }
            return user;
        }
    }

    @Override
    public Optional<User> getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery(GET_USER_BY_USERNAME, User.class);
            query.setParameter("username", username);
            return query.uniqueResultOptional();
        }
    }

    @Override
    public User create(User user) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
            return user;
        }
    }

    @Override
    public User update(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
            return user;
        }
    }

    @Override
    public void delete(int userId) {
        User user = getById(userId);
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(user);
            session.getTransaction().commit();
        }
    }
}
