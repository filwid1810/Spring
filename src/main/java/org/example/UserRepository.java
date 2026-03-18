package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository {
    List<User> users = new ArrayList<>();
    String filePath;

    @Override
    public User getUser(String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) {
                return user.copy();
            }else{
                System.out.println(user.getLogin()
                + " not found");
            }
        }
        return null;
    }

    @Override
    public List<User> getUsers() {
        List<User> copyUsers = new ArrayList<>();
        for (User user : users) {
            copyUsers.add(user.copy());
        }
        return copyUsers;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public void save() {
            try(PrintWriter writer = new PrintWriter(filePath)) {
                for(User v:users){
                    writer.println(v.getLogin());
                }
            }catch (IOException e){
                System.out.println("Error saving file");
            }

        }

    @Override
    public void load() {

    }
}
