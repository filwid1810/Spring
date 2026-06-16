package com.umcsuser.carrent.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Profile("jdbc")
public class VehicleJdbcRepository implements VehicleRepository {

    private final DataSource dataSource;
    private final Gson gson = new Gson();
    private final Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

    public VehicleJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Vehicle mapRowToVehicle(ResultSet rs) throws SQLException {
        String attributesJson = rs.getString("attributes");
        Map<String, Object> attributes = new HashMap<>();
        if (attributesJson != null && !attributesJson.isBlank()) {
            attributes = gson.fromJson(attributesJson, mapType);
        }

        return Vehicle.builder()
                .id(rs.getString("id"))
                .category(rs.getString("category"))
                .brand(rs.getString("brand"))
                .model(rs.getString("model"))
                .year(rs.getInt("year"))
                .plate(rs.getString("plate"))
                .price(rs.getDouble("price"))
                .attributes(attributes)
                .build();
    }

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT id, category, brand, model, year, plate, price, attributes FROM vehicle";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                vehicles.add(mapRowToVehicle(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd odczytu pojazdów", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return vehicles;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        String sql = "SELECT * FROM vehicle WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd szukania pojazdu", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(java.util.UUID.randomUUID().toString());
        }
        String sql = "INSERT INTO vehicle (id, category, brand, model, year, plate, price, attributes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "category = EXCLUDED.category, brand = EXCLUDED.brand, model = EXCLUDED.model, " +
                "year = EXCLUDED.year, plate = EXCLUDED.plate, price = EXCLUDED.price, attributes = EXCLUDED.attributes";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM vehicle WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd usuwania pojazdu", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}