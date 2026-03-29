package org.example;

public class User {
    String login;
    String password;
    Role role;
    String rentedVehicles ;

    public User(String login, String password, Role role, String rentedVehicleId) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicles = rentedVehicleId;
    }

    public String toCSV() {
        return login + ";" + password + ";" + role.name() + ";" ;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", rentedVehicles='" + rentedVehicles + '\'' +
                '}';
    }
    public User copy() {
        return new User(this.login, this.password, this.role, rentedVehicles);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getRentedVehicles() {
        return rentedVehicles;
    }

    public void setRentedVehicles(String rentedVehicles) {
        this.rentedVehicles = rentedVehicles;
    }
}
