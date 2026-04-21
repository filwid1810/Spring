package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;
    private final RentalService rentalService;

    public UserService(UserRepository userRepository, RentalService rentalService) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void removeUser(String loginOrId) {
        Optional<User> userOpt = userRepository.findByLogin(loginOrId);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findById(loginOrId);
        }

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Nie znaleziono użytkownika.");
        }

        User targetUser = userOpt.get();

        if (rentalService.userHasActiveRental(targetUser.getId())) {
            throw new IllegalStateException("Nie można usunąć użytkownika, ponieważ posiada on aktywny wynajem.");
        }

        userRepository.deleteById(targetUser.getId());
    }
}