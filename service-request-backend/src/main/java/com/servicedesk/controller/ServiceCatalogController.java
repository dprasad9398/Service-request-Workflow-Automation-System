package com.servicedesk.controller;

import com.servicedesk.dto.ApiResponse;
import com.servicedesk.entity.ServiceCatalog;
import com.servicedesk.entity.ServiceCategory;
import com.servicedesk.service.ServiceCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Service Catalog Controller
 * REST APIs for service catalog management
 */
@RestController
@RequestMapping("/services")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ServiceCatalogController {

    @Autowired
    private ServiceCatalogService serviceCatalogService;

    /**
     * Get all categories
     * GET /api/services/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<ServiceCategory>> getAllCategories() {
        List<ServiceCategory> categories = serviceCatalogService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Get category by ID
     * GET /api/services/categories/{id}
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            ServiceCategory category = serviceCatalogService.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error fetching category: " + e.getMessage()));
        }
    }

    /**
     * Get all services
     * GET /api/services
     */
    @GetMapping
    public ResponseEntity<List<ServiceCatalog>> getAllServices() {
        List<ServiceCatalog> services = serviceCatalogService.getActiveServices();
        return ResponseEntity.ok(services);
    }

    /**
     * Get services by category
     * GET /api/services/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ServiceCatalog>> getServicesByCategory(@PathVariable Long categoryId) {
        List<ServiceCatalog> services = serviceCatalogService.getServicesByCategory(categoryId);
        return ResponseEntity.ok(services);
    }

    /**
     * Get service by ID
     * GET /api/services/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable Long id) {
        try {
            ServiceCatalog service = serviceCatalogService.getServiceById(id);
            return ResponseEntity.ok(service);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error fetching service: " + e.getMessage()));
        }
    }

    /**
     * Create category (Admin only)
     * POST /api/services/categories
     */
    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody ServiceCategory category) {
        try {
            ServiceCategory created = serviceCatalogService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error creating category: " + e.getMessage()));
        }
    }

    /**
     * Create service (Admin only)
     * POST /api/services
     */
    @PostMapping
    public ResponseEntity<?> createService(@RequestBody ServiceCatalog service) {
        try {
            ServiceCatalog created = serviceCatalogService.createService(service);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error creating service: " + e.getMessage()));
        }
    }

    /**
     * Toggle service status (Admin only)
     * PATCH /api/services/{id}/toggle
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleServiceStatus(@PathVariable Long id) {
        try {
            ServiceCatalog service = serviceCatalogService.toggleServiceStatus(id);
            return ResponseEntity.ok(service);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error toggling service: " + e.getMessage()));
        }
    }
}
