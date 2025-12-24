package com.servicedesk.service;

import com.servicedesk.entity.RequestType;
import com.servicedesk.entity.ServiceCategory;
import com.servicedesk.repository.RequestTypeRepository;
import com.servicedesk.repository.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing request categories and types
 */
@Service
@Transactional
public class CategoryService {

    @Autowired
    private ServiceCategoryRepository categoryRepository;

    @Autowired
    private RequestTypeRepository requestTypeRepository;

    /**
     * Get all active categories
     */
    public List<ServiceCategory> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByNameAsc();
    }

    /**
     * Get category by ID
     */
    public ServiceCategory getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    /**
     * Get all request types for a category
     */
    public List<RequestType> getTypesByCategory(Long categoryId) {
        System.out.println("=== FETCHING REQUEST TYPES ===");
        System.out.println("Category ID: " + categoryId);

        try {
            List<RequestType> types = requestTypeRepository.findByCategoryIdAndIsActiveTrue(categoryId);
            System.out.println("Found " + types.size() + " types for category " + categoryId);
            return types;
        } catch (Exception e) {
            System.err.println("Error fetching types for category " + categoryId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch request types", e);
        }
    }

    /**
     * Get request type by ID
     */
    public RequestType getTypeById(Long id) {
        return requestTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request type not found with id: " + id));
    }

    /**
     * Get all active request types
     */
    public List<RequestType> getAllActiveTypes() {
        return requestTypeRepository.findByIsActiveTrue();
    }
}
