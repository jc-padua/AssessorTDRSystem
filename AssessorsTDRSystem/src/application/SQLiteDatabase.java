package application;

import java.sql.*;

public class SQLiteDatabase {
	
	public static void main(String[] args) {
		SQLiteDatabase connection = new SQLiteDatabase();
		
	}
	
	public void openDB() {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:src/assessors.db");
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened Database Successfully");
	}
	
	public void createDB() {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite/assessors.db");
			System.out.println("Opened Database Successfully");
			
			stmt = conn.createStatement();
			String sql = "CREATE TABLE taxDec"
					+ "(pin TEXT PRIMARY KEY NOT NULL,"
					+ "series_number TEXT NOT NULL,"
					+ "owner TEXT NOT NULL,"
					+ "location TEXT NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();
			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
	        System.exit(0);
	    }
	    System.out.println("Table created successfully");
	}
}
