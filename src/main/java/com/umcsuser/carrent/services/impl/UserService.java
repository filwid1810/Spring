package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.services.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class  UserService implements UserServiceInterface {
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private final UserRepository userRepo;
    private final RentalRepository rentalRepo;

    public UserService(UserRepository userRepo, RentalRepository rentalRepo) {
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(String id) {
        return userRepo.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByLogin(String login) {
        return userRepo.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika z loginem: " + login));
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {
        boolean hasActiveRental = rentalRepo.findAll().stream()
                .anyMatch(r -> id.equals(r.getUserId()) && r.isActive());

        if (hasActiveRental) {
            throw new IllegalStateException("Nie można usunąć użytkownika z aktywnym wynajmem.");
        }
        userRepo.deleteById(id);
    }
    @Override
    public void register(String login, String password) {
        if (userRepo.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Użytkownik o takim loginie już istnieje!");
        }

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPasswordHash(passwordEncoder.encode(password));
        newUser.setRole(com.umcsuser.carrent.models.Role.USER);

        userRepo.save(newUser);
    }
}