package org.example;


public class Main {
    public static void main(String[] args) {
        IVehicleRepository vehicleRepo = new VehicleRepository();
        IUserRepository userRepo = new UserRepository();
        Authentication auth = new Authentication(userRepo);
        Ui ui = new Ui(vehicleRepo, userRepo, auth);


        ui.run();
    }
}