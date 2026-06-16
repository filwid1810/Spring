package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.services.VehicleServiceInterface;
import com.umcsuser.carrent.services.VehicleValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VehicleService implements VehicleServiceInterface {

    private final VehicleRepository vehicleRepo;

    private final RentalRepository rentalRepo;
    private final VehicleValidator validator;


    public VehicleService(VehicleRepository vehicleRepo, RentalRepository rentalRepo, VehicleValidator validator) {
        this.vehicleRepo = vehicleRepo;
        this.rentalRepo = rentalRepo;
        this.validator = validator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAllVehicles() {
        return vehicleRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepo.findAll().stream()
                .filter(v -> rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicle findById(String id) {
        return vehicleRepo.findById(id).orElse(null);
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        validator.validate(vehicle);
        return vehicleRepo.save(vehicle);
    }

    @Override
    public void removeVehicle(String vehicleId) {
        if (isVehicleRented(vehicleId)) {
            throw new IllegalStateException("Nie można usunąć pojazdu, bo jest wypożyczony.");
        }
        vehicleRepo.deleteById(vehicleId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVehicleRented(String vehicleId) {
        return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}