package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.services.AuthServiceInterface;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Optional;
import java.util.UUID;

public class SimpleAuthService implements AuthServiceInterface {
    private final UserRepository userRepository;

    public SimpleAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean register(String login, String rawPassword) {
        if (userRepository.findByLogin(login).isPresent()) {
            return false;
        }
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .login(login)
                .passwordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return true;
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isPresent() && BCrypt.checkpw(rawPassword, userOpt.get().getPasswordHash())) {
            return userOpt;
        }
        return Optional.empty();
    }
}