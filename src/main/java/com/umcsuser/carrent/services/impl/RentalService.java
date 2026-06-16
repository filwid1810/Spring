package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.services.RentalServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RentalService implements RentalServiceInterface {

    private final RentalRepository rentalRepo;
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;

    public RentalService(RentalRepository rentalRepo, VehicleRepository vehicleRepo, UserRepository userRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    private Rental populateRental(Rental rental) {
        if (rental != null) {
            if (rental.getVehicleId() != null) {
                vehicleRepo.findById(rental.getVehicleId()).ifPresent(rental::setVehicle);
            }
            if (rental.getUserId() != null) {
                userRepo.findById(rental.getUserId()).ifPresent(rental::setUser);
            }
        }
        return rental;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        if (userHasActiveRental(userId)) {
            throw new IllegalStateException("Masz już aktywne wypożyczenie.");
        }

        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Pojazd nie istnieje."));

        if (vehicleHasActiveRental(vehicleId)) {
            throw new IllegalStateException("Ten pojazd jest już wypożyczony.");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Użytkownik nie istnieje."));

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .vehicle(vehicle)
                .user(user)
                .rentDateTime(LocalDateTime.now().toString())
                .build();

        Rental savedRental = rentalRepo.save(rental);
        return populateRental(savedRental);
    }

    @Override
    public Rental returnVehicle(String userId) {
        Rental rental = findActiveRentalByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Nie masz wypożyczonego pojazdu."));

        rental.setReturnDateTime(LocalDateTime.now().toString());
        Rental savedRental = rentalRepo.save(rental);

        return populateRental(savedRental);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()) && r.isActive())
                .map(this::populateRental)
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findAllRentals() {
        return rentalRepo.findAll().stream()
                .map(this::populateRental)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findUserRentals(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .map(this::populateRental)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasActiveRental(String userId) {
        return rentalRepo.findAll().stream()
                .anyMatch(r -> userId.equals(r.getUserId()) && r.isActive());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}