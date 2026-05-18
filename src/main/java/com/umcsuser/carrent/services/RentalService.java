package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class RentalService {

    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    public boolean userHasActiveRental(String userId) {
        return rentalRepository.findAll().stream()
                .anyMatch(r -> r.getUserId().equals(userId) && r.isActive());
    }

    public void rentVehicle(String userId, String vehicleId) {
        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .user(User.builder().id(userId).build())
                .vehicle(Vehicle.builder().id(vehicleId).build())
                .rentDateTime(LocalDateTime.now().toString())
                .build();
        rentalRepository.save(rental);
    }

    public void returnVehicle(String userId) {
        rentalRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.isActive())
                .findFirst()
                .ifPresentOrElse(rental -> {
                    rental.setReturnDateTime(LocalDateTime.now().toString());
                    rentalRepository.save(rental);
                }, () -> {
                    throw new IllegalStateException("Nie masz żadnego aktywnego wypożyczenia.");
                });
    }

    public Optional<Rental> getActiveRentalForUser(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.isActive())
                .findFirst();
    }
}