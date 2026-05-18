package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.db.HibernateConfig;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.impl.RentalHibernateRepository;
import com.umcsuser.carrent.repositories.impl.UserHibernateRepository;
import com.umcsuser.carrent.services.UserServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class UserHibernateService implements UserServiceInterface {
    private final UserHibernateRepository userRepo;
    private final RentalHibernateRepository rentalRepo;

    public UserHibernateService(UserHibernateRepository userRepo, RentalHibernateRepository rentalRepo) {
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
    }

    @Override
    public List<User> findAllUsers() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepo.setSession(session);
            return userRepo.findAll();
        }
    }

    @Override
    public User findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepo.setSession(session);
            return userRepo.findById(id).orElse(null);
        }
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            userRepo.setSession(session);
            rentalRepo.setSession(session);

            boolean hasActive = rentalRepo.findAll().stream()
                    .anyMatch(r -> id.equals(r.getUserId()) && r.isActive());
            if (hasActive) {
                throw new IllegalStateException("Użytkownik posiada aktywny wynajem.");
            }

            userRepo.deleteById(id);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}