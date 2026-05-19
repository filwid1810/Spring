package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.services.RentalServiceInterface;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SimpleRentalService implements RentalServiceInterface {
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public SimpleRentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        if (userHasActiveRental(userId)) {
            throw new IllegalStateException("Masz już aktywne wypożyczenie.");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElse(Vehicle.builder().id(vehicleId).build());

        if (vehicleHasActiveRental(vehicleId)) {
            throw new IllegalStateException("Ten pojazd jest już wypożyczony.");
        }

        User user = userRepository.findById(userId)
                .orElse(User.builder().id(userId).build());

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .vehicle(vehicle)
                .user(user)
                .rentDateTime(LocalDateTime.now().toString())
                .build();
        return rentalRepository.save(rental);
    }

    @Override
    public Rental returnVehicle(String userId) {
        Rental rental = findActiveRentalByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Nie masz wypożyczonego pojazdu."));
        rental.setReturnDateTime(LocalDateTime.now().toString());
        return rentalRepository.save(rental);
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()) && r.isActive())
                .findFirst();
    }

    @Override
    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .toList();
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}