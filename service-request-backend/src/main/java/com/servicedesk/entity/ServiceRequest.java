package com.servicedesk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * ServiceRequest Entity
 * Main entity representing service requests raised by users
 */
@Entity
@Table(name = "service_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false, unique = true, length = 50)
    private String ticketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceCatalog service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RequestStatus status = RequestStatus.NEW;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    /**
     * Priority Enum
     */
    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    /**
     * Request Status Enum
     * Represents the complete lifecycle of a service request
     */
    public enum RequestStatus {
        NEW,
        PENDING_APPROVAL,
        APPROVED,
        REJECTED,
        ASSIGNED,
        IN_PROGRESS,
        RESOLVED,
        CLOSED,
        CANCELLED
    }
}
