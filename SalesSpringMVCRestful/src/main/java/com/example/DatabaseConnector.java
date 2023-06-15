package com.example;

import java.sql.*;

import org.springframework.beans.factory.annotation.Value;

public class DatabaseConnector {
	 	@Value("${db.url}")
	    private static String dbUrl;

	    @Value("${db.username}")
	    private static String dbUsername;

	    @Value("${db.password}")
	    private static String dbPassword;
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
}
