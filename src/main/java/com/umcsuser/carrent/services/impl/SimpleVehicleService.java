package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.services.VehicleServiceInterface;
import com.umcsuser.carrent.services.VehicleValidator;
import java.util.List;

public class SimpleVehicleService implements VehicleServiceInterface {
    private final VehicleValidator vehicleValidator;
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    public SimpleVehicleService(VehicleValidator vehicleValidator, VehicleRepository vehicleRepository, RentalRepository rentalRepository) {
        this.vehicleValidator = vehicleValidator;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(v -> rentalRepository.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                .toList();
    }

    @Override
    public Vehicle findById(String id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        return vehicleRepository.save(vehicle);
    }

    @Override
    public void removeVehicle(String vehicleId) {
        boolean rented = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        if (rented) {
            throw new IllegalStateException("Nie można usunąć pojazdu, bo jest aktualnie wypożyczony.");
        }
        vehicleRepository.deleteById(vehicleId);
    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}