package com.smartservice.enums;

public enum Role {
    ADMIN,
    AGENT,
    USER;

    public static Role fromString(String role) {
        if (role == null) return null;
        try {
            return Role.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
