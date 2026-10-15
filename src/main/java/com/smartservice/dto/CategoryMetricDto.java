package com.smartservice.dto;

import java.io.Serializable;

public class CategoryMetricDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String categoryName;
    private long totalRequests;
    private long resolvedRequests;
    private double avgResolutionHours;

    public CategoryMetricDto() {}

    public CategoryMetricDto(String categoryName, long totalRequests, long resolvedRequests, double avgResolutionHours) {
        this.categoryName = categoryName;
        this.totalRequests = totalRequests;
        this.resolvedRequests = resolvedRequests;
        this.avgResolutionHours = avgResolutionHours;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getResolvedRequests() {
        return resolvedRequests;
    }

    public void setResolvedRequests(long resolvedRequests) {
        this.resolvedRequests = resolvedRequests;
    }

    public double getAvgResolutionHours() {
        return avgResolutionHours;
    }

    public void setAvgResolutionHours(double avgResolutionHours) {
        this.avgResolutionHours = avgResolutionHours;
    }
}
