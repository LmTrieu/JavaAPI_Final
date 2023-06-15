package com.example.controller;

import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.security.*;
import java.sql.*;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {

    private final String DB_URL = "jdbc:mysql://localhost:3306/shop_management";
    private final String DB_USERNAME = "root";
    private final String DB_PASSWORD = "123456";

    @PostMapping
    public ResponseEntity<String> authenticatePost(@RequestBody AuthenticationRequest authenticationRequest) {
        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        try {
            String hashedPassword = hashPassword(password);

            if (authenticateUser(username, hashedPassword)) {
                return ResponseEntity.ok("Authentication successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed. Please check your username or password.");
            }
        } catch (HttpMessageNotReadableException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request body. Please provide a valid JSON object.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred. Please try again.");
        }
    }




    @GetMapping
    public ResponseEntity<String> authenticateGet(@RequestParam String username, @RequestParam String password) {
        String hashedPassword = hashPassword(password);
        if (authenticateUser(username, hashedPassword)) {
            return ResponseEntity.ok("Authentication successful.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed. Please check your username or password.");
        }
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = messageDigest.digest(password.getBytes());
            StringBuilder stringBuilder = new StringBuilder();
            for (byte hashedByte : hashedBytes) {
                stringBuilder.append(Integer.toString((hashedByte & 0xff) + 0x100, 16).substring(1));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class AuthenticationRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
