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

        VehicleService vehicleService = new VehicleService(validator, vehicleRepo, rentalRepo);
        AuthService authService = new AuthService(userRepo);

        UI ui = new UI(vehicleService, configService, userRepo, rentalRepo, authService);
        ui.start();
    }
}