package com.umcsuser.carrent.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JdbcConnectionManager;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.sql.*;
import java.util.*;

public class VehicleJdbcRepository implements VehicleRepository {
    private final Gson gson = new Gson();

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicle";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToVehicle(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania pojazdów", e);
        }
        return list;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        String sql = "SELECT * FROM vehicle WHERE id = ?";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToVehicle(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd wyszukiwania pojazdu", e);
        }
        return Optional.empty();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
            insert(vehicle);
        } else {
            if (findById(vehicle.getId()).isPresent()) {
                update(vehicle);
            } else {
                insert(vehicle);
            }
        }
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM vehicle WHERE id = ?";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd usuwania pojazdu", e);
        }
    }

    private void insert(Vehicle vehicle) {
        String sql = "INSERT INTO vehicle (id, category, brand, model, year, plate, price, attributes) VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb)";
        executeSaveQuery(sql, vehicle);
    }

    private void update(Vehicle vehicle) {
        String sql = "UPDATE vehicle SET category=?, brand=?, model=?, year=?, plate=?, price=?, attributes=?::jsonb WHERE id=?";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getCategory());
            stmt.setString(2, vehicle.getBrand());
            stmt.setString(3, vehicle.getModel());
            stmt.setInt(4, vehicle.getYear());
            stmt.setString(5, vehicle.getPlate());
            stmt.setDouble(6, vehicle.getPrice());
            stmt.setString(7, gson.toJson(vehicle.getAttributes()));
            stmt.setString(8, vehicle.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd podczas aktualizacji pojazdu", e);
        }
    }

    private void executeSaveQuery(String sql, Vehicle vehicle) {
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getId());
            stmt.setString(2, vehicle.getCategory());
            stmt.setString(3, vehicle.getBrand());
            stmt.setString(4, vehicle.getModel());
            stmt.setInt(5, vehicle.getYear());
            stmt.setString(6, vehicle.getPlate());
            stmt.setDouble(7, vehicle.getPrice());
            stmt.setString(8, gson.toJson(vehicle.getAttributes()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu pojazdu", e);
        }
    }

    private Vehicle mapRowToVehicle(ResultSet rs) throws SQLException {
        String attrJson = rs.getString("attributes");
        Map<String, Object> attributes = gson.fromJson(attrJson, new TypeToken<Map<String, Object>>(){}.getType());
        return Vehicle.builder()
                .id(rs.getString("id"))
                .category(rs.getString("category"))
                .brand(rs.getString("brand"))
                .model(rs.getString("model"))
                .year(rs.getInt("year"))
                .plate(rs.getString("plate"))
                .price(rs.getDouble("price"))
                .attributes(attributes != null ? attributes : new HashMap<>())
                .build();
    }
}