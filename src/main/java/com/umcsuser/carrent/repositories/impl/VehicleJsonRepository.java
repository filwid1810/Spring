package com.umcsuser.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("json")
public class VehicleJsonRepository implements VehicleRepository {

    private final JsonFileStorage<Vehicle> storage;
    private List<Vehicle> vehicles;

    public VehicleJsonRepository() {
        this.storage = new JsonFileStorage<>("vehicles.json", new TypeToken<List<Vehicle>>(){}.getType());
        this.vehicles = storage.load();
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicles.stream().map(Vehicle::copy).collect(Collectors.toList());
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream()
                .filter(v -> v.getId().equals(id))
                .map(Vehicle::copy)
                .findFirst();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        deleteById(vehicle.getId());
        vehicles.add(vehicle.copy());
        storage.save(vehicles);
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        vehicles.removeIf(v -> v.getId().equals(id));
        storage.save(vehicles);
    }
}
