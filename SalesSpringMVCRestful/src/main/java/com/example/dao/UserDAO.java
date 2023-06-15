package com.example.dao;

import com.example.model.User;

public interface UserDAO {
    User getPasswordByUsername(String username);
	boolean updateUser(User user);
}
