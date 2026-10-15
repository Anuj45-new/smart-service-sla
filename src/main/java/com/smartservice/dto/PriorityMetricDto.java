package com.smartservice.dto;

import com.smartservice.enums.Priority;

import java.io.Serializable;

public class PriorityMetricDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Priority priority;
    private long totalCount;
    private long breachedCount;
    private double avgResolutionHours;

    public PriorityMetricDto() {}

    public PriorityMetricDto(Priority priority, long totalCount, long breachedCount, double avgResolutionHours) {
        this.priority = priority;
        this.totalCount = totalCount;
        this.breachedCount = breachedCount;
        this.avgResolutionHours = avgResolutionHours;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getBreachedCount() {
        return breachedCount;
    }

    public void setBreachedCount(long breachedCount) {
        this.breachedCount = breachedCount;
    }

    public double getAvgResolutionHours() {
        return avgResolutionHours;
    }

    public void setAvgResolutionHours(double avgResolutionHours) {
        this.avgResolutionHours = avgResolutionHours;
    }
}
