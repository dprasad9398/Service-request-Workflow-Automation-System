package com.servicedesk.dto;

import com.servicedesk.entity.ServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Service Request Creation DTO
 */
@Data
public class ServiceRequestDTO {

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Priority is required")
    private ServiceRequest.Priority priority;
}
