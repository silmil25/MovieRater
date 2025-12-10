package com.web.movierater.repositories;

import com.web.movierater.exceptions.EntityNotFoundException;
import com.web.movierater.models.Movie;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MovieRepositoryDbImpl implements MovieRepository {

    public static final String GET_ALL_MOVIES =
            "FROM Movie";

    public static final String GET_USER_BY_TITLE =
            "from Movie where title = :title";



    private final SessionFactory sessionFactory;

    @Autowired
    public MovieRepositoryDbImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public List<Movie> get() {
        try (Session session = sessionFactory.openSession()) {
            Query<Movie> query =session.createQuery(GET_ALL_MOVIES, Movie.class);
            return query.list();
        }
    }

    @Override
    public Movie getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Movie movie = session.get(Movie.class, id);
            if (movie == null) {
                throw new EntityNotFoundException("Movie", id);
            }
            return movie;
        }
    }

    @Override
    public Optional<Movie> getByTitle(String title) {
        try (Session session = sessionFactory.openSession()) {
            Query<Movie> query = session.createQuery(GET_USER_BY_TITLE, Movie.class);
            query.setParameter("title", title);
            return query.uniqueResultOptional();
        }
    }

    @Override
    public Movie create(Movie movie) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(movie);
            session.getTransaction().commit();
            return movie;
        }

    }

    @Override
    public Movie update(Movie movie) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(movie);
            session.getTransaction().commit();
            return movie;
        }
    }

    @Override
    public void delete(int movieId) {
        Movie movie = getById(movieId);
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(movie);
            session.getTransaction().commit();
        }
    }
}
