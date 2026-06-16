package com.umcsuser.carrent.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class  JdbcConnectionManager {
    private static JdbcConnectionManager instance;
    private final String url;

    private JdbcConnectionManager() {
        url = System.getenv("DB_URL");
        if (url == null) {
            throw new RuntimeException("Zmienna środowiskowa DB_URL nie jest ustawiona!");
        }
    }

    public static JdbcConnectionManager getInstance() {
        if (instance == null) {
            instance = new JdbcConnectionManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException("Błąd połączenia z bazą danych!", e);
        }
    }
}