package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;
import java.util.Optional;

public interface  AuthServiceInterface {
    boolean register(String login, String rawPassword);
    Optional<User> login(String login, String rawPassword);
}