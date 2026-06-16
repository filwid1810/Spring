package com.umcsuser.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Repository
@Profile("json")
public class RentalJsonRepository implements RentalRepository {

    private final JsonFileStorage<Rental> storage;
    private List<Rental> rentals;


    public RentalJsonRepository() {
        this.storage = new JsonFileStorage<>("rentals.json", new TypeToken<List<Rental>>(){}.getType());
        this.rentals = storage.load();
    }
    @Override
    public List<Rental> findAll() {
        return rentals.stream().map(Rental::copy).collect(Collectors.toList());
    }

    @Override
    public Optional<Rental> findById(String id) {
        return rentals.stream()
                .filter(r -> r.getId().equals(id))
                .map(Rental::copy)
                .findFirst();
    }

    @Override
    public Rental save(Rental rental) {
        deleteById(rental.getId());
        rentals.add(rental.copy());
        storage.save(rentals);
        return rental;
    }

    @Override
    public void deleteById(String id) {
        rentals.removeIf(r -> r.getId().equals(id));
        storage.save(rentals);
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return rentals.stream()
                .filter(r -> r.getVehicleId().equals(vehicleId) && r.isActive())
                .map(Rental::copy)
                .findFirst();
    }
}
