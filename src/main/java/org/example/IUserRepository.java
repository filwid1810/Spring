package org.example;

import java.util.List;

public interface IUserRepository {
    User getUser(String login);
    List<User> getUsers();
    boolean update(User user);
    boolean register(User user);
    boolean delete(String login);

}
