package com.smartservice.enums;

public enum Status {
    OPEN,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED,
    ESCALATED;

    public boolean isTerminal() {
        return this == RESOLVED || this == CLOSED;
    }

    public static Status fromString(String status) {
        if (status == null) return null;
        try {
            return Status.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
