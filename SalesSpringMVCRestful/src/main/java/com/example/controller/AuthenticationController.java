package com.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.security.*;
import java.sql.*;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @PostMapping
    public ResponseEntity<String> authenticatePost(@RequestBody AuthenticationRequest authenticationRequest) {
        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        try {
            String hashedPassword = hashPassword(password);

            if (isUsernameValid(username)) {
                if (authenticateUser(username, hashedPassword)) {
                    return ResponseEntity.ok("Authentication successful");
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed. Please check your password.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed. Invalid username.");
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
        
        if (!isUsernameValid(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username does not exist.");
        }

        if (authenticateUser(username, hashedPassword)) {
            return ResponseEntity.ok("Authentication successful.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password.");
        }
    }

    private boolean isUsernameValid(String username) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0; // Trả về true nếu username tồn tại trong cơ sở dữ liệu
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Trả về false nếu xảy ra lỗi hoặc username không tồn tại
    }


	private boolean authenticateUser(String username, String password) {
    	try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
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
