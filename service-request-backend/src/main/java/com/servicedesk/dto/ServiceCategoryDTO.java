package com.servicedesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Service Category
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private String department;
    private Boolean isActive;
    private Integer serviceCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
