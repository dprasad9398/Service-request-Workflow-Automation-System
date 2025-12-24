package com.servicedesk.controller;

import com.servicedesk.entity.Department;
import com.servicedesk.entity.User;
import com.servicedesk.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Department Controller
 * Handles department-related operations for admin
 */
@RestController
@RequestMapping("/admin/departments")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * Get all active departments
     * GET /api/admin/departments
     */
    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        System.out.println("=== ADMIN: Fetching all departments ===");

        List<Department> departments = departmentService.getAllActiveDepartments();
        return ResponseEntity.ok(departments);
    }

    /**
     * Get agents by department
     * GET /api/admin/departments/{id}/agents
     */
    @GetMapping("/{id}/agents")
    public ResponseEntity<List<User>> getAgentsByDepartment(@PathVariable Long id) {
        System.out.println("=== ADMIN: Fetching agents for department " + id + " ===");

        List<User> agents = departmentService.getAgentsByDepartment(id);
        return ResponseEntity.ok(agents);
    }

    /**
     * Get all agents
     * GET /api/admin/departments/agents
     */
    @GetMapping("/agents")
    public ResponseEntity<List<User>> getAllAgents() {
        System.out.println("=== ADMIN: Fetching all agents ===");

        List<User> agents = departmentService.getAllAgents();
        return ResponseEntity.ok(agents);
    }
}
