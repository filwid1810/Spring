package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.services.AuthServiceInterface;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class  AuthService implements AuthServiceInterface {

    private final UserRepository userRepo;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public boolean register(String login, String rawPassword) {
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
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> login(String login, String rawPassword) {
        Optional<User> userOpt = userRepo.findByLogin(login);
        if (userOpt.isPresent() && BCrypt.checkpw(rawPassword, userOpt.get().getPasswordHash())) {
            return userOpt;
        }
        return Optional.empty();
    }
}