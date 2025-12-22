package com.servicedesk.service;

import com.servicedesk.entity.ServiceCatalog;
import com.servicedesk.entity.ServiceCategory;
import com.servicedesk.exception.ResourceNotFoundException;
import com.servicedesk.repository.ServiceCatalogRepository;
import com.servicedesk.repository.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Catalog Service
 * Business logic for service catalog management
 */
@Service
@Transactional
public class ServiceCatalogService {

    @Autowired
    private ServiceCatalogRepository serviceCatalogRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    public List<ServiceCategory> getAllCategories() {
        return serviceCategoryRepository.findAll();
    }

    public List<ServiceCategory> getActiveCategories() {
        return serviceCategoryRepository.findByIsActive(true);
    }

    public ServiceCategory getCategoryById(Long id) {
        return serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceCategory", "id", id));
    }

    public ServiceCategory createCategory(ServiceCategory category) {
        return serviceCategoryRepository.save(category);
    }

    public ServiceCategory updateCategory(Long id, ServiceCategory categoryDetails) {
        ServiceCategory category = getCategoryById(id);
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setIcon(categoryDetails.getIcon());
        category.setIsActive(categoryDetails.getIsActive());
        return serviceCategoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        ServiceCategory category = getCategoryById(id);
        serviceCategoryRepository.delete(category);
    }

    public List<ServiceCatalog> getAllServices() {
        return serviceCatalogRepository.findAll();
    }

    public List<ServiceCatalog> getActiveServices() {
        return serviceCatalogRepository.findByIsActive(true);
    }

    public List<ServiceCatalog> getServicesByCategory(Long categoryId) {
        return serviceCatalogRepository.findByCategoryIdAndIsActive(categoryId, true);
    }

    public ServiceCatalog getServiceById(Long id) {
        return serviceCatalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceCatalog", "id", id));
    }

    public ServiceCatalog createService(ServiceCatalog service) {
        return serviceCatalogRepository.save(service);
    }

    public ServiceCatalog updateService(Long id, ServiceCatalog serviceDetails) {
        ServiceCatalog service = getServiceById(id);
        service.setName(serviceDetails.getName());
        service.setDescription(serviceDetails.getDescription());
        service.setCategory(serviceDetails.getCategory());
        service.setSla(serviceDetails.getSla());
        service.setIsActive(serviceDetails.getIsActive());
        service.setRequiresApproval(serviceDetails.getRequiresApproval());
        return serviceCatalogRepository.save(service);
    }

    public ServiceCatalog toggleServiceStatus(Long id) {
        ServiceCatalog service = getServiceById(id);
        service.setIsActive(!service.getIsActive());
        return serviceCatalogRepository.save(service);
    }

    public void deleteService(Long id) {
        ServiceCatalog service = getServiceById(id);
        serviceCatalogRepository.delete(service);
    }
}
