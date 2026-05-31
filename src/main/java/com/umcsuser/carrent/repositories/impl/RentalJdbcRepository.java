package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class RentalJdbcRepository implements RentalRepository {

    private final DataSource dataSource;

    public RentalJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
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

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rentals.add(mapRowToRental(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd odczytu wypożyczeń", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql = "SELECT * FROM rental WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToRental(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd szukania wypożyczenia", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT * FROM rental WHERE vehicle_id = ? AND return_date IS NULL";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToRental(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd szukania aktywnego wypożyczenia", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        String sql = "INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "return_date = EXCLUDED.return_date";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, rental.getId());
            stmt.setString(2, rental.getVehicleId());
            stmt.setString(3, rental.getUserId());
            stmt.setString(4, rental.getRentDateTime());
            stmt.setString(5, rental.getReturnDateTime());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu wypożyczenia", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return rental;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd usuwania wypożyczenia", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}