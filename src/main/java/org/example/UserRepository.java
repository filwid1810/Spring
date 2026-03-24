package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserRepository implements IUserRepository {
    List<User> users = new ArrayList<>();
    String filePath = "users.csv";

    public UserRepository() {
        load(this.filePath);
    }

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



    private void save(String filePath) {
            try(PrintWriter writer = new PrintWriter(filePath)) {
                for(User user:users){
                   String vehicleId = (user.getRentedVehicles() == null)? "" : user.getRentedVehicles();
                    writer.println(user.getLogin()+";" + user.getPassword()+";"+ user.getRole().name() + ";"+vehicleId);
                }
            }catch (IOException e){
                System.out.println("Error saving file");
            }

        }


    private void load(String filePath) {
       try {
           File file = new File(filePath);
           Scanner scannerf = new Scanner(file);
           users.clear();
           while (scannerf.hasNextLine()) {
               String line = scannerf.nextLine();
               String[] split = line.split(";",-1);
               if(split.length>=4){
                   String rentedVehicleId = split[3].isEmpty() ? null : split[3];
                   users.add(new User(split[0], split[1], Role.valueOf(split[2]), rentedVehicleId));

               }
           }
           scannerf.close();
       }catch(FileNotFoundException e){
           System.out.println("File not found"+ filePath);
       }

    }
    @Override
    public boolean update(User user) {
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getLogin().equals(user.getLogin())){
                User existingUser = users.get(i);
                existingUser.setPassword(user.getPassword());
                existingUser.setRole(user.getRole());
                existingUser.setRentedVehicles(user.getRentedVehicles());
                save(filePath);
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean register(User user) {

        for (User u : users) {
            if (u.getLogin().equals(user.getLogin())) {
                System.out.println( "jest taki user");
                return false;
            }
        }
        users.add(user);
        save("users.csv");
        System.out.println("udało się dodac usera ");

        return true;
    }

    @Override
    public boolean delete(String login) {
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getLogin().equals(login)) {

                String rented = u.getRentedVehicles();
                if (rented != null && !rented.isEmpty()) {

                    System.out.println( "user ma wyporzyczony pojazd");
                    return false;
                }
                users.remove(i);
                save("users.csv");
                System.out.println("udało się usunąć usera");
                return true;

            }
        }
        System.out.println("nie znaleziono usera ");
        return false;

    }
}
