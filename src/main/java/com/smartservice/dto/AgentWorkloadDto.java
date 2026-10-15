package com.smartservice.dto;

import java.io.Serializable;

public class AgentWorkloadDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long agentId;
    private String agentName;
    private String agentEmail;
    private long totalAssigned;
    private long openCount;
    private long inProgressCount;
    private long resolvedCount;
    private long breachedCount;

    public AgentWorkloadDto() {}

    public AgentWorkloadDto(Long agentId, String agentName, String agentEmail,
                            long totalAssigned, long openCount, long inProgressCount,
                            long resolvedCount, long breachedCount) {
        this.agentId = agentId;
        this.agentName = agentName;
        this.agentEmail = agentEmail;
        this.totalAssigned = totalAssigned;
        this.openCount = openCount;
        this.inProgressCount = inProgressCount;
        this.resolvedCount = resolvedCount;
        this.breachedCount = breachedCount;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentEmail() {
        return agentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }

    public long getTotalAssigned() {
        return totalAssigned;
    }

    public void setTotalAssigned(long totalAssigned) {
        this.totalAssigned = totalAssigned;
    }

    public long getOpenCount() {
        return openCount;
    }

    public void setOpenCount(long openCount) {
        this.openCount = openCount;
    }

    public long getInProgressCount() {
        return inProgressCount;
    }

    public void setInProgressCount(long inProgressCount) {
        this.inProgressCount = inProgressCount;
    }

    public long getResolvedCount() {
        return resolvedCount;
    }

    public void setResolvedCount(long resolvedCount) {
        this.resolvedCount = resolvedCount;
    }

    public long getBreachedCount() {
        return breachedCount;
    }

    public void setBreachedCount(long breachedCount) {
        this.breachedCount = breachedCount;
    }
}
