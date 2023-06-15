package com.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.model.User;

public class UserDAOImpl implements UserDAO {
	
    private final String DB_URL = "jdbc:mysql://localhost:3306/shop_management";
    private final String DB_USERNAME = "root";
    private final String DB_PASSWORD = "123456";
	
	@Override
	public User getPasswordByUsername(String username) {
	    String query = "SELECT password FROM users WHERE username = ?";
	    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
	         PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setString(1, username);
	        
	        try (ResultSet resultSet = statement.executeQuery()) {
	            if (resultSet.next()) {
	                String retrievedPassword = resultSet.getString("password");
	                // Thực hiện việc khởi tạo đối tượng User từ dữ liệu truy vấn
	                User user = new User(username, retrievedPassword);
	                return user;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	public boolean updateUser(User user) {
        String query = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getPassword());
            statement.setString(2, user.getUsername());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
