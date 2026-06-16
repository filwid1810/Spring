package com.umcsuser.carrent.repositories.impl;


import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Repository
@Profile("json")
public class UserJsonRepository implements UserRepository {

    private final JsonFileStorage<User> storage;
    private List<User> users;

    public UserJsonRepository() {
        this.storage = new JsonFileStorage<>("users.json", new TypeToken<List<User>>(){}.getType());
        this.users = storage.load();
    }

    @Override
    public List<User> findAll() {

        return users.stream().map(User::copy).collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .map(User::copy)
                .findFirst();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.stream()
                .filter(u -> u.getLogin().equals(login))
                .map(User::copy)
                .findFirst();
    }

    @Override
    public User save(User user) {
        deleteById(user.getId());
        users.add(user.copy());
        storage.save(users);
        return user;
    }

    @Override
    public void deleteById(String id) {
        users.removeIf(u -> u.getId().equals(id));
        storage.save(users);
    }
}
