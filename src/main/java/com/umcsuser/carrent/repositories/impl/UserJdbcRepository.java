package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.db.JdbcConnectionManager;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;

import java.sql.*;
import java.util.*;

public class UserJdbcRepository implements UserRepository {

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania użytkowników", e);
        }
        return list;
    }

    @Override
    public Optional<User> findById(String id) {
        return findByColumn("id", id);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return findByColumn("login", login);
    }

    private Optional<User> findByColumn(String column, String value) {
        String sql = "SELECT * FROM users WHERE " + column + " = ?";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd wyszukiwania użytkownika", e);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }

        if (findById(user.getId()).isPresent()) {
            String sql = "UPDATE users SET login=?, password_hash=?, role=? WHERE id=?";
            try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getLogin());
                stmt.setString(2, user.getPasswordHash());
                stmt.setString(3, user.getRole().name());
                stmt.setString(4, user.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Błąd aktualizacji użytkownika", e);
            }
        } else {
            String sql = "INSERT INTO users (id, login, password_hash, role) VALUES (?, ?, ?, ?)";
            try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getId());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getPasswordHash());
                stmt.setString(4, user.getRole().name());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Błąd zapisu użytkownika", e);
            }
        }
        return user;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd usuwania użytkownika", e);
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("id"),
                rs.getString("login"),
                rs.getString("password_hash"),
                Role.valueOf(rs.getString("role"))
        );
    }
}