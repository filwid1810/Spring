package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class UserHibernateRepository implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> findAll() {
        return entityManager.createQuery("FROM User", User.class).getResultList();
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByLogin(String login) {
        return entityManager.createQuery("FROM User WHERE login = :login", User.class)
                .setParameter("login", login)
                .getResultStream()
                .findFirst();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }
        return entityManager.merge(user);
    }

    @Override
    public void deleteById(String id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }
}