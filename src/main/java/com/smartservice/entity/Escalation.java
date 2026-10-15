package com.smartservice.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "escalations")
public class Escalation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private ServiceRequest request;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(name = "breached_at", nullable = false)
    private LocalDateTime breachedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Escalation() {
    }

    public Escalation(ServiceRequest request, String reason, LocalDateTime breachedAt) {
        this.request = request;
        this.reason = reason;
        this.breachedAt = breachedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceRequest getRequest() {
        return request;
    }

    public void setRequest(ServiceRequest request) {
        this.request = request;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getBreachedAt() {
        return breachedAt;
    }

    public void setBreachedAt(LocalDateTime breachedAt) {
        this.breachedAt = breachedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Escalation that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
