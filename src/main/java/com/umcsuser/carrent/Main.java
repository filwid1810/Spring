package com.umcsuser.carrent;

import com.umcsuser.carrent.repositories.*;
import com.umcsuser.carrent.repositories.impl.*;
import com.umcsuser.carrent.services.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- START SYSTEMU ---");

        VehicleRepository vehicleRepo;
        UserRepository userRepo;
        RentalRepository rentalRepo;

        if (args.length > 0 && args[0].equalsIgnoreCase("jdbc")) {
            System.out.println("[INFO] Wybrano bazę danych: PostgreSQL (JDBC)");
            vehicleRepo = new VehicleJdbcRepository();
            userRepo = new UserJdbcRepository();
            rentalRepo = new RentalJdbcRepository();
        } else {
            System.out.println("[INFO] Wybrano bazę danych: Pliki lokalne (JSON)");
            vehicleRepo = new VehicleJsonRepository();
            userRepo = new UserJsonRepository();
            rentalRepo = new RentalJsonRepository();
        }

        VehicleCategoryConfigJsonRepository configRepo = new VehicleCategoryConfigJsonRepository();
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