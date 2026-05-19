package com.umcsuser.carrent;

import com.umcsuser.carrent.repositories.impl.*;
import com.umcsuser.carrent.services.*;
import com.umcsuser.carrent.services.impl.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- START SYSTEMU ---");

        VehicleServiceInterface vehicleService;
        RentalServiceInterface rentalService;
        UserServiceInterface userService;
        AuthServiceInterface authService;

        VehicleCategoryConfigJsonRepository configRepo = new VehicleCategoryConfigJsonRepository();
        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        VehicleValidator validator = new VehicleValidator(configService);

        System.out.println("[INFO] Wybrano bazę danych: ORM Hibernate native");

        VehicleHibernateRepository vehicleRepo = new VehicleHibernateRepository();
        UserHibernateRepository userRepo = new UserHibernateRepository();
        RentalHibernateRepository rentalRepo = new RentalHibernateRepository();

        rentalService = new RentalHibernateService(rentalRepo, vehicleRepo, userRepo);
        vehicleService = new VehicleHibernateService(vehicleRepo, rentalRepo, validator);
        userService = new UserHibernateService(userRepo, rentalRepo);
        authService = new AuthHibernateService(userRepo);

        UI ui = new UI(vehicleService, configService, userService, rentalService, authService);
        ui.start();
    }
}