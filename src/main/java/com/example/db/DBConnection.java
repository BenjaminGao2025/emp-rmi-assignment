package com.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Centralizes SQLite connection creation and runtime pragmas.
 */
public final class DBConnection {
    private static final int BUSY_TIMEOUT_MS = 5000;

    private DBConnection() {
    }

    public static Connection getConnection(String dbPath) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        applyConnectionPragmas(conn);
        return conn;
    }

    public static void initializeDatabase(String dbPath) throws SQLException {
        try (Connection conn = getConnection(dbPath);
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL");
        }
    }

    private static void applyConnectionPragmas(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys=ON");
            stmt.execute("PRAGMA busy_timeout=" + BUSY_TIMEOUT_MS);
        }
    }
}
