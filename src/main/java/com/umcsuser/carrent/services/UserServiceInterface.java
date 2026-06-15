package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;

import java.util.List;

public interface UserServiceInterface {

    List<User> findAllUsers();

    User findById(String id);
    User findByLogin(String login);

    void register(String login, String password);

    void deleteUser(String id, String loggedUserId);
}