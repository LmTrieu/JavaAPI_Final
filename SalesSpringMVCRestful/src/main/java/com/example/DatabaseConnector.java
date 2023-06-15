package com.example;

import java.sql.*;

import org.springframework.beans.factory.annotation.Value;

public class DatabaseConnector {
	@Value("${db.url}")
    private static String URL;
	@Value("${db.username}")
    private static String USERNAME;
	@Value("${db.password}")
    private static String PASSWORD;
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
