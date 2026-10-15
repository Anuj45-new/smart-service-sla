package com.smartservice.util;

import com.smartservice.enums.Priority;

import java.time.LocalDateTime;

/**
 * Utility for SLA deadline computation and breach evaluation.
 * SLA policies:
 * - CRITICAL: 2 hours
 * - HIGH:     6 hours
 * - MEDIUM:  24 hours
 * - LOW:     72 hours
 */
public final class SlaCalculator {

    private SlaCalculator() {}

    /**
     * Calculates the SLA deadline from a given start timestamp.
     */
    public static LocalDateTime calculateDeadline(LocalDateTime startTime, Priority priority) {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (priority == null) {
            priority = Priority.MEDIUM;
        }
        return startTime.plusHours(priority.getDefaultSlaHours());
    }

    /**
     * Determines whether the current time has surpassed the SLA deadline.
     */
    public static boolean isBreached(LocalDateTime deadline, LocalDateTime now) {
        if (deadline == null) {
            return false;
        }
        if (now == null) {
            now = LocalDateTime.now();
        }
        return now.isAfter(deadline);
    }
}
