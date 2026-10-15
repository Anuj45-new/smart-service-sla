package com.smartservice.service;

import com.smartservice.dao.UserDao;
import com.smartservice.entity.User;
import com.smartservice.enums.Role;
import com.smartservice.exception.AppException;
import com.smartservice.exception.AuthenticationException;
import com.smartservice.util.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Authenticates a user with username and plain-text password.
     */
    public User authenticate(String username, String plainPassword) {
        if (username == null || username.trim().isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
            throw new AuthenticationException("Username and password are required");
        }

        User user = userDao.findByUsername(username.trim())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!PasswordHasher.checkPassword(plainPassword, user.getPasswordHash())) {
            logger.warn("Authentication failed for user: {}", username);
            throw new AuthenticationException("Invalid username or password");
        }

        logger.info("User authenticated successfully: {} with role: {}", user.getUsername(), user.getRole());
        return user;
    }

    public User getUserById(Long id) {
        if (id == null) {
            throw new AppException("User ID cannot be null");
        }
        User user = userDao.findById(id);
        if (user == null) {
            throw new AppException("User not found with ID: " + id);
        }
        return user;
    }

    public List<User> getAllAgents() {
        return userDao.findByRole(Role.AGENT);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }
}
