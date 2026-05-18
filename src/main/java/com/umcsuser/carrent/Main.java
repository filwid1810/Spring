package com.umcsuser.carrent;

import com.umcsuser.carrent.repositories.impl.*;
import com.umcsuser.carrent.services.*;
import com.umcsuser.carrent.services.impl.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- START SYSTEMU ---");
        System.out.println("[INFO] Wybrano bazę danych: ORM Hibernate native");

        VehicleCategoryConfigJsonRepository configRepo = new VehicleCategoryConfigJsonRepository();
        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        VehicleValidator validator = new VehicleValidator(configService);

        VehicleHibernateRepository vehicleRepo = new VehicleHibernateRepository();
        UserHibernateRepository userRepo = new UserHibernateRepository();
        RentalHibernateRepository rentalRepo = new RentalHibernateRepository();

        RentalServiceInterface rentalService = new RentalHibernateService(rentalRepo, vehicleRepo, userRepo);
        VehicleServiceInterface vehicleService = new VehicleHibernateService(vehicleRepo, rentalRepo, validator);
        UserServiceInterface userService = new UserHibernateService(userRepo, rentalRepo);
        AuthServiceInterface authService = new AuthHibernateService(userRepo);

        UI ui = new UI(vehicleService, configService, userService, rentalService, authService);
        ui.start();
    }
}