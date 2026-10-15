package com.smartservice.dto;

import java.io.Serializable;

public class DashboardSummaryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private long totalRequests;
    private long openRequests;
    private long inProgressRequests;
    private long resolvedRequests;
    private long closedRequests;
    private long escalatedRequests;
    private long slaBreachedRequests;

    private long lowPriorityCount;
    private long mediumPriorityCount;
    private long highPriorityCount;
    private long criticalPriorityCount;

    public DashboardSummaryDto() {}

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getOpenRequests() {
        return openRequests;
    }

    public void setOpenRequests(long openRequests) {
        this.openRequests = openRequests;
    }

    public long getInProgressRequests() {
        return inProgressRequests;
    }

    public void setInProgressRequests(long inProgressRequests) {
        this.inProgressRequests = inProgressRequests;
    }

    public long getResolvedRequests() {
        return resolvedRequests;
    }

    public void setResolvedRequests(long resolvedRequests) {
        this.resolvedRequests = resolvedRequests;
    }

    public long getClosedRequests() {
        return closedRequests;
    }

    public void setClosedRequests(long closedRequests) {
        this.closedRequests = closedRequests;
    }

    public long getEscalatedRequests() {
        return escalatedRequests;
    }

    public void setEscalatedRequests(long escalatedRequests) {
        this.escalatedRequests = escalatedRequests;
    }

    public long getSlaBreachedRequests() {
        return slaBreachedRequests;
    }

    public void setSlaBreachedRequests(long slaBreachedRequests) {
        this.slaBreachedRequests = slaBreachedRequests;
    }

    public long getLowPriorityCount() {
        return lowPriorityCount;
    }

    public void setLowPriorityCount(long lowPriorityCount) {
        this.lowPriorityCount = lowPriorityCount;
    }

    public long getMediumPriorityCount() {
        return mediumPriorityCount;
    }

    public void setMediumPriorityCount(long mediumPriorityCount) {
        this.mediumPriorityCount = mediumPriorityCount;
    }

    public long getHighPriorityCount() {
        return highPriorityCount;
    }

    public void setHighPriorityCount(long highPriorityCount) {
        this.highPriorityCount = highPriorityCount;
    }

    public long getCriticalPriorityCount() {
        return criticalPriorityCount;
    }

    public void setCriticalPriorityCount(long criticalPriorityCount) {
        this.criticalPriorityCount = criticalPriorityCount;
    }
}
