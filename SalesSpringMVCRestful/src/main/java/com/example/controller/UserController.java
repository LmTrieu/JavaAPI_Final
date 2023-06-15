package com.example.controller;

import com.example.model.User;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final String DB_URL = "jdbc:mysql://localhost:3306/shop_management";
    private final String DB_USERNAME = "root";
    private final String DB_PASSWORD = "123456";

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Map<Integer, User>> getUsers() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {

            Map<Integer, User> users = new HashMap<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                User user = new User(id, username, password);
                users.put(id, user);
            }

            return ResponseEntity.ok(users);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable("id") int id) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                	String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    User user = new User(id, username, password);
                    return ResponseEntity.ok(user);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateUser(@PathVariable("id") int id, @RequestBody User updatedUser) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET username = ?, password = ? WHERE id = ?")) {
            statement.setString(1, updatedUser.getUsername());
            statement.setString(2, updatedUser.getPassword());
            statement.setInt(3, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return ResponseEntity.ok("User updated successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUser(@PathVariable("id") int id) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return ResponseEntity.ok("User deleted successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addUser(@RequestBody User newUser) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (id, username, password) VALUES (?, ?, ?)")) {
            statement.setInt(1, newUser.getId());
            statement.setString(2, newUser.getUsername());
            statement.setString(3, newUser.getPassword());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return ResponseEntity.ok("User added successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
