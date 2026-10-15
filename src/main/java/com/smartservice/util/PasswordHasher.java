package com.smartservice.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for secure BCrypt password hashing and verification.
 */
public final class PasswordHasher {

    private static final int LOG_ROUNDS = 12;

    private PasswordHasher() {}

    /**
     * Hashes a plain-text password using BCrypt with a secure salt.
     *
     * @param plainPassword Plain-text password to hash
     * @return 60-character BCrypt hash string
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * Verifies that a plain-text password matches a stored BCrypt hash.
     *
     * @param plainPassword Plain-text candidate password
     * @param hashedPassword Stored BCrypt hash
     * @return true if password matches, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Malformed hash
            return false;
        }
    }
}
