package com.umcsuser.carrent;

import com.umcsuser.carrent.repositories.impl.*;
import com.umcsuser.carrent.services.*;

public class Main {
    public static void main(String[] args) {

        VehicleCategoryConfigJsonRepository configRepo = new VehicleCategoryConfigJsonRepository();
        VehicleJsonRepository vehicleRepo = new VehicleJsonRepository();
        UserJsonRepository userRepo = new UserJsonRepository();
        RentalJsonRepository rentalRepo = new RentalJsonRepository();

        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        VehicleValidator validator = new VehicleValidator(configService);

        RentalService rentalService = new RentalService(rentalRepo);
        VehicleService vehicleService = new VehicleService(validator, vehicleRepo, rentalRepo);
        UserService userService = new UserService(userRepo, rentalService);
        AuthService authService = new AuthService(userRepo);

        UI ui = new UI(vehicleService, configService, userService, rentalService, authService);
        ui.start();
    }
}