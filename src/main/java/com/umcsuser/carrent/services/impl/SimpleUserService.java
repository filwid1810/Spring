package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.services.UserServiceInterface;
import java.util.List;

public class SimpleUserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public SimpleUserService(UserRepository userRepository, RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {
        boolean hasActiveRental = rentalRepository.findAll().stream()
                .anyMatch(r -> id.equals(r.getUserId()) && r.isActive());

        if (hasActiveRental) {
            throw new IllegalStateException("Nie można usunąć użytkownika, ponieważ posiada on aktywny wynajem.");
        }
        userRepository.deleteById(id);
    }
}