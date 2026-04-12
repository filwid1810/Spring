package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

public class AuthService {
    private final UserRepository userRepo;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public boolean register(String login, String plainPassword) {

        if (userRepo.findByLogin(login).isPresent()) {
            return false;
        }

        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        User newUser = User.builder()
                .id(UUID.randomUUID().toString())
                .login(login)
                .passwordHash(hashedPassword)
                .role(Role.USER)
                .build();

        userRepo.save(newUser);
        return true;
    }

    public Optional<User> login(String login, String plainPassword) {

        Optional<User> userOpt = userRepo.findByLogin(login);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }


        return Optional.empty();
    }
}
