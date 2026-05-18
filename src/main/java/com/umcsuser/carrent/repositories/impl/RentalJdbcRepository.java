package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.db.JdbcConnectionManager;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;

import java.sql.*;
import java.util.*;

public class RentalJdbcRepository implements RentalRepository {

    @Override
    public List<Rental> findAll() {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT * FROM rental";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToRental(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania wypożyczeń", e);
        }
        return list;
    }

    @Override
    public Optional<Rental> findById(String id) {
        return Optional.empty();
    }

    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT * FROM rental WHERE vehicle_id = ? AND return_date IS NULL";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToRental(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd sprawdzania dostępności auta", e);
        }
        return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        if (rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(UUID.randomUUID().toString());
        }

        if (rental.getReturnDateTime() != null) {
            String sql = "UPDATE rental SET return_date=? WHERE id=?";
            try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, rental.getReturnDateTime());
                stmt.setString(2, rental.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Błąd zapisu zwrotu", e);
            }
        } else {
            String sql = "INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, rental.getId());
                stmt.setString(2, rental.getVehicleId());
                stmt.setString(3, rental.getUserId());
                stmt.setString(4, rental.getRentDateTime());
                stmt.setString(5, rental.getReturnDateTime());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Błąd zapisu wypożyczenia", e);
            }
        }
        return rental;
    }

    @Override
    public void deleteById(String id) {
    }

    private Rental mapRowToRental(ResultSet rs) throws SQLException {

        Vehicle tempVehicle = Vehicle.builder().id(rs.getString("vehicle_id")).build();
        User tempUser = User.builder().id(rs.getString("user_id")).build();

        return Rental.builder()
                .id(rs.getString("id"))
                .vehicle(tempVehicle)
                .user(tempUser)
                .rentDateTime(rs.getString("rent_date"))
                .returnDateTime(rs.getString("return_date"))
                .build();
    }
}