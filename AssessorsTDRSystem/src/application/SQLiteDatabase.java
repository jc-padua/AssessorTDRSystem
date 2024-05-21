package application;

import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDatabase {

    private static final String DB_URL = "jdbc:sqlite:src/assessors.db";

    public static void main(String[] args) {
        SQLiteDatabase database = new SQLiteDatabase();
        database.createDB();
    }

    public Connection openDB() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Opened Database Successfully");
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return conn;
    }

    public void createDB() {
        String sql = "CREATE TABLE IF NOT EXISTS taxDec (" +
                     "pin TEXT PRIMARY KEY NOT NULL, " +
                     "series_number TEXT NOT NULL, " +
                     "owner TEXT NOT NULL, " +
                     "location TEXT NOT NULL, " +
                     "archive INTEGER DEFAULT 0" +
                     ");";
        
        try (Connection conn = openDB(); Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.executeUpdate(sql);
                System.out.println("Table created successfully");
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}