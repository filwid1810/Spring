package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.db.HibernateConfig;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.impl.UserHibernateRepository;
import com.umcsuser.carrent.services.AuthServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Optional;
import java.util.UUID;

public class AuthHibernateService implements AuthServiceInterface {
    private final UserHibernateRepository userRepo;

    public AuthHibernateService(UserHibernateRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public boolean register(String login, String rawPassword) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            userRepo.setSession(session);

            if (userRepo.findByLogin(login).isPresent()) {
                return false;
            }

            User user = User.builder()
                    .id(UUID.randomUUID().toString())
                    .login(login)
                    .passwordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()))
                    .role(Role.USER)
                    .build();

            userRepo.save(user);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepo.setSession(session);
            Optional<User> userOpt = userRepo.findByLogin(login);
            if (userOpt.isPresent() && BCrypt.checkpw(rawPassword, userOpt.get().getPasswordHash())) {
                return userOpt;
            }
            return Optional.empty();
        }
    }
}