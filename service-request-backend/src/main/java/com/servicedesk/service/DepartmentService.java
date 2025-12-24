package com.servicedesk.service;

import com.servicedesk.entity.CategoryDepartmentMapping;
import com.servicedesk.entity.Department;
import com.servicedesk.entity.User;
import com.servicedesk.repository.CategoryDepartmentMappingRepository;
import com.servicedesk.repository.DepartmentRepository;
import com.servicedesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing departments
 */
@Service
@Transactional
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CategoryDepartmentMappingRepository mappingRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all active departments
     */
    public List<Department> getAllActiveDepartments() {
        return departmentRepository.findByIsActiveTrue();
    }

    /**
     * Get department by ID
     */
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    /**
     * Get department for a category (auto-assignment logic)
     */
    public Optional<Department> getDepartmentForCategory(Long categoryId) {
        return mappingRepository.findByCategoryId(categoryId)
                .map(CategoryDepartmentMapping::getDepartment);
    }

    /**
     * Get agents by department
     * Returns users with ROLE_AGENT or ROLE_ADMIN
     */
    public List<User> getAgentsByDepartment(Long departmentId) {
        // For now, return all agents
        // In a full implementation, you'd have a department_users table
        return getAllAgents();
    }

    /**
     * Get all agents (users with ROLE_AGENT)
     */
    public List<User> getAllAgents() {
        // Return all users for now - in production, filter by role
        return userRepository.findAll();
    }
}
