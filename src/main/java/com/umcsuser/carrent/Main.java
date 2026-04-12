package com.umcsuser.carrent;

import com.umcsuser.carrent.repositories.*;
import com.umcsuser.carrent.repositories.impl.*;
import com.umcsuser.carrent.services.AuthService;

public class Main {
    public static void main(String[] args) {
        VehicleRepository vehicleRepo = new VehicleJsonRepository();
        UserRepository userRepo = new UserJsonRepository();
        RentalRepository rentalRepo = new RentalJsonRepository();

        AuthService authService = new AuthService(userRepo);

        Ui ui = new Ui(vehicleRepo, userRepo, rentalRepo, authService);
        ui.start();
    }
}