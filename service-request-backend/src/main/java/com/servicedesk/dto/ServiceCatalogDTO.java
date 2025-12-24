package com.servicedesk.dto;

import com.servicedesk.entity.ServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Service Catalog
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCatalogDTO {
    private Long id;

    @NotBlank(message = "Service name is required")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String categoryName;

    private ServiceRequest.Priority defaultPriority;

    private String department;

    private Integer slaHours;

    private Long slaId;

    private String slaName;

    private Boolean isActive;

    private Boolean requiresApproval;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
