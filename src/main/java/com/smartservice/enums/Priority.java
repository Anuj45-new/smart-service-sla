package com.smartservice.enums;

public enum Priority {
    LOW(72),
    MEDIUM(24),
    HIGH(6),
    CRITICAL(2);

    private final int defaultSlaHours;

    Priority(int defaultSlaHours) {
        this.defaultSlaHours = defaultSlaHours;
    }

    public int getDefaultSlaHours() {
        return defaultSlaHours;
    }

    public static Priority fromString(String priority) {
        if (priority == null) return null;
        try {
            return Priority.valueOf(priority.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
