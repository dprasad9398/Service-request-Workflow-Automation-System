package com.servicedesk.controller;

import com.servicedesk.dto.AdminRequestDTO;
import com.servicedesk.dto.AssignRequestDTO;
import com.servicedesk.dto.UpdateStatusDTO;
import com.servicedesk.entity.RequestStatusHistory;
import com.servicedesk.service.AdminRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin Request Management Controller
 * Handles admin operations for managing service requests
 */
@RestController
@RequestMapping("/admin/requests")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRequestController {

    @Autowired
    private AdminRequestService adminRequestService;

    /**
     * Get all requests with filtering and pagination
     * GET /api/admin/requests?category=IT&department=IT
     * Support&priority=HIGH&status=NEW&page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRequests(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        System.out.println("=== ADMIN: Fetching requests ===");
        System.out.println("Filters - Category: " + category + ", Department: " + department +
                ", Priority: " + priority + ", Status: " + status);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AdminRequestDTO> requests = adminRequestService.getAllRequests(
                category, department, priority, status, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("requests", requests.getContent());
        response.put("currentPage", requests.getNumber());
        response.put("totalItems", requests.getTotalElements());
        response.put("totalPages", requests.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * Assign request to department
     * POST /api/admin/requests/{id}/assign-department
     */
    @PostMapping("/{id}/assign-department")
    public ResponseEntity<?> assignDepartment(
            @PathVariable Long id,
            @RequestBody AssignRequestDTO dto) {
        System.out.println("=== ADMIN: Assigning department to request " + id + " ===");

        try {
            adminRequestService.assignDepartment(id, dto);
            return ResponseEntity.ok(Map.of("success", true, "message", "Department assigned successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Assign request to agent
     * POST /api/admin/requests/{id}/assign-agent
     */
    @PostMapping("/{id}/assign-agent")
    public ResponseEntity<?> assignAgent(
            @PathVariable Long id,
            @RequestBody AssignRequestDTO dto) {
        System.out.println("=== ADMIN: Assigning agent to request " + id + " ===");

        try {
            adminRequestService.assignAgent(id, dto);
            return ResponseEntity.ok(Map.of("success", true, "message", "Agent assigned successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Update request status
     * PUT /api/admin/requests/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusDTO dto) {
        System.out.println("=== ADMIN: Updating status for request " + id + " ===");

        try {
            adminRequestService.updateStatus(id, dto);
            return ResponseEntity.ok(Map.of("success", true, "message", "Status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Get request timeline (status history)
     * GET /api/admin/requests/{id}/timeline
     */
    @GetMapping("/{id}/timeline")
    public ResponseEntity<List<RequestStatusHistory>> getTimeline(@PathVariable Long id) {
        System.out.println("=== ADMIN: Fetching timeline for request " + id + " ===");

        List<RequestStatusHistory> timeline = adminRequestService.getRequestTimeline(id);
        return ResponseEntity.ok(timeline);
    }
}
