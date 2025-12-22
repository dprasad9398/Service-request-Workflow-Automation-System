package com.servicedesk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * SLA (Service Level Agreement) Entity
 * Defines response and resolution time expectations
 */
@Entity
@Table(name = "sla")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SLA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "response_time_hours", nullable = false)
    private Integer responseTimeHours;

    @Column(name = "resolution_time_hours", nullable = false)
    private Integer resolutionTimeHours;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SLA(String name, Integer responseTimeHours, Integer resolutionTimeHours, String description) {
        this.name = name;
        this.responseTimeHours = responseTimeHours;
        this.resolutionTimeHours = resolutionTimeHours;
        this.description = description;
    }
}
