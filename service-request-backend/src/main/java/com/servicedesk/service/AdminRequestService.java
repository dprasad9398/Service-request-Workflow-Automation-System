package com.servicedesk.service;

import com.servicedesk.dto.*;
import com.servicedesk.entity.*;
import com.servicedesk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for admin request management operations
 */
@Service
@Transactional
public class AdminRequestService {

    @Autowired
    private ServiceRequestRepository requestRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryDepartmentMappingRepository mappingRepository;

    @Autowired
    private RequestStatusHistoryRepository statusHistoryRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    /**
     * Get all requests with filtering and pagination
     */
    public Page<AdminRequestDTO> getAllRequests(
            String category,
            String department,
            String priority,
            String status,
            Pageable pageable) {
        Specification<ServiceRequest> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null && !category.isEmpty() && !category.equals("ALL")) {
                predicates.add(cb.equal(root.get("category").get("name"), category));
            }

            if (department != null && !department.isEmpty() && !department.equals("ALL")) {
                predicates.add(cb.equal(root.get("department").get("name"), department));
            }

            if (priority != null && !priority.isEmpty() && !priority.equals("ALL")) {
                predicates.add(cb.equal(root.get("priority"), ServiceRequest.Priority.valueOf(priority)));
            }

            if (status != null && !status.isEmpty() && !status.equals("ALL")) {
                predicates.add(cb.equal(root.get("status"), ServiceRequest.RequestStatus.valueOf(status)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<ServiceRequest> requests = requestRepository.findAll(spec, pageable);
        List<AdminRequestDTO> dtos = requests.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, requests.getTotalElements());
    }

    /**
     * Assign request to department
     */
    public AdminRequestDTO assignDepartment(Long requestId, AssignDepartmentDTO dto) {
        // Validate input
        if (dto.getDepartmentId() == null) {
            throw new IllegalArgumentException("Department ID cannot be null");
        }

        // Fetch request
        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with ID: " + requestId));

        // Fetch department
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Department not found with ID: " + dto.getDepartmentId()));

        // Check if department is active
        if (!department.getIsActive()) {
            throw new IllegalArgumentException("Cannot assign to inactive department: " + department.getName());
        }

        // Assign department
        request.setDepartment(department);
        request.setStatus(ServiceRequest.RequestStatus.ASSIGNED);
        ServiceRequest savedRequest = requestRepository.save(request);

        // Log status change
        logStatusChange(savedRequest, "Assigned to department: " + department.getName() +
                (dto.getNotes() != null && !dto.getNotes().isEmpty() ? ". Notes: " + dto.getNotes() : ""));

        System.out.println(
                "✓ Request " + savedRequest.getTicketId() + " assigned to department: " + department.getName());

        return convertToDTO(savedRequest);
    }

    /**
     * Assign request to agent
     */
    public void assignAgent(Long requestId, AssignRequestDTO dto) {
        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        User agent = userRepository.findById(dto.getAgentId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        request.setAssignedAgent(agent);
        request.setStatus(ServiceRequest.RequestStatus.IN_PROGRESS);
        requestRepository.save(request);

        // Create task for agent
        createTaskForAgent(request, agent);

        // Log status change
        logStatusChange(request, "Assigned to agent: " + agent.getUsername() +
                (dto.getNotes() != null ? ". Notes: " + dto.getNotes() : ""));

        System.out.println("Request " + request.getTicketId() + " assigned to agent: " + agent.getUsername());
    }

    /**
     * Update request status
     */
    public void updateStatus(Long requestId, UpdateStatusDTO dto) {
        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        ServiceRequest.RequestStatus oldStatus = request.getStatus();
        ServiceRequest.RequestStatus newStatus = ServiceRequest.RequestStatus.valueOf(dto.getStatus());

        request.setStatus(newStatus);
        requestRepository.save(request);

        // Log status change
        logStatusChange(request, "Status changed from " + oldStatus + " to " + newStatus +
                (dto.getNotes() != null ? ". Notes: " + dto.getNotes() : ""));

        System.out.println("Request " + request.getTicketId() + " status updated to: " + newStatus);
    }

    /**
     * Auto-assign department based on category
     */
    public void autoAssignDepartment(ServiceRequest request) {
        if (request.getCategory() == null) {
            return;
        }

        mappingRepository.findByCategoryId(request.getCategory().getId())
                .ifPresent(mapping -> {
                    request.setDepartment(mapping.getDepartment());
                    System.out.println("Auto-assigned department: " + mapping.getDepartment().getName());
                });
    }

    /**
     * Get request timeline (status history)
     */
    public List<RequestStatusHistory> getRequestTimeline(Long requestId) {
        return statusHistoryRepository.findByRequestIdOrderByChangedAtDesc(requestId);
    }

    // Private helper methods

    private AdminRequestDTO convertToDTO(ServiceRequest request) {
        AdminRequestDTO dto = new AdminRequestDTO();
        dto.setId(request.getId());
        dto.setTicketId(request.getTicketId());
        dto.setUserName(request.getRequester() != null ? request.getRequester().getUsername() : "N/A");
        dto.setUserEmail(request.getRequester() != null ? request.getRequester().getEmail() : "N/A");
        dto.setCategoryName(request.getCategory() != null ? request.getCategory().getName() : "N/A");
        dto.setTypeName(request.getRequestType() != null ? request.getRequestType().getName() : "N/A");
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setPriority(request.getPriority().toString());
        dto.setStatus(request.getStatus().toString());
        dto.setDepartmentName(request.getDepartment() != null ? request.getDepartment().getName() : "Unassigned");
        dto.setAssignedAgentName(
                request.getAssignedAgent() != null ? request.getAssignedAgent().getUsername() : "Unassigned");
        dto.setAssignedAgentEmail(request.getAssignedAgent() != null ? request.getAssignedAgent().getEmail() : null);
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        return dto;
    }

    private void logStatusChange(ServiceRequest request, String notes) {
        RequestStatusHistory history = new RequestStatusHistory();
        history.setRequest(request);
        history.setFromStatus(request.getStatus().toString());
        history.setToStatus(request.getStatus().toString());

        // Set changed_by - use requester as fallback if no authenticated user
        // TODO: Get from SecurityContext when authentication is properly configured
        if (request.getRequester() != null) {
            history.setChangedBy(request.getRequester());
        }

        history.setRemarks(notes);
        statusHistoryRepository.save(history);
    }

    private void createTaskForAgent(ServiceRequest request, User agent) {
        // Simplified - just log for now
        // In production, you'd create actual Task entity
        System.out.println("Task would be created for agent: " + agent.getUsername());
        System.out.println("Request: " + request.getTicketId());
        System.out.println("Due date: " + calculateDueDate(request.getPriority()));
    }

    private LocalDateTime calculateDueDate(ServiceRequest.Priority priority) {
        LocalDateTime now = LocalDateTime.now();
        return switch (priority) {
            case CRITICAL -> now.plusHours(4);
            case HIGH -> now.plusDays(1);
            case MEDIUM -> now.plusDays(3);
            case LOW -> now.plusDays(7);
        };
    }

    /**
     * Update request priority
     */
    public AdminRequestDTO updatePriority(Long requestId, UpdatePriorityDTO dto) {
        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with ID: " + requestId));

        ServiceRequest.Priority oldPriority = request.getPriority();
        ServiceRequest.Priority newPriority = ServiceRequest.Priority.valueOf(dto.getPriority());

        request.setPriority(newPriority);
        ServiceRequest savedRequest = requestRepository.save(request);

        // Log activity
        logActivity(savedRequest, "PRIORITY_CHANGE", oldPriority.toString(), newPriority.toString(), dto.getNotes());

        System.out.println(
                "✓ Request " + savedRequest.getTicketId() + " priority updated: " + oldPriority + " → " + newPriority);

        return convertToDTO(savedRequest);
    }

    /**
     * Escalate request
     */
    public AdminRequestDTO escalateRequest(Long requestId, EscalateRequestDTO dto) {
        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with ID: " + requestId));

        StringBuilder escalationDetails = new StringBuilder("Reason: " + dto.getReason());

        // Escalate to department if specified
        if (dto.getEscalateToDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getEscalateToDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found"));
            request.setDepartment(department);
            escalationDetails.append(", Escalated to Department: ").append(department.getName());
        }

        // Escalate to agent if specified
        if (dto.getEscalateToAgentId() != null) {
            User agent = userRepository.findById(dto.getEscalateToAgentId())
                    .orElseThrow(() -> new IllegalArgumentException("Agent not found"));
            request.setAssignedAgent(agent);
            escalationDetails.append(", Escalated to Agent: ").append(agent.getUsername());
        }

        // Increase priority if not already critical
        if (request.getPriority() != ServiceRequest.Priority.CRITICAL) {
            ServiceRequest.Priority oldPriority = request.getPriority();
            request.setPriority(ServiceRequest.Priority.HIGH);
            escalationDetails.append(", Priority increased: ").append(oldPriority).append(" → HIGH");
        }

        ServiceRequest savedRequest = requestRepository.save(request);

        // Log escalation
        logActivity(savedRequest, "ESCALATE", null, escalationDetails.toString(), dto.getNotes());

        System.out.println("✓ Request " + savedRequest.getTicketId() + " escalated");

        return convertToDTO(savedRequest);
    }

    /**
     * Get comprehensive request details
     */
    public RequestDetailsDTO getRequestDetails(Long requestId) {
        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with ID: " + requestId));

        RequestDetailsDTO dto = new RequestDetailsDTO();

        // Request info
        dto.setId(request.getId());
        dto.setTicketId(request.getTicketId());
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setPriority(request.getPriority().toString());
        dto.setStatus(request.getStatus().toString());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());

        // Requester info
        if (request.getRequester() != null) {
            dto.setRequesterId(request.getRequester().getId());
            dto.setRequesterName(request.getRequester().getFirstName() + " " + request.getRequester().getLastName());
            dto.setRequesterEmail(request.getRequester().getEmail());
            dto.setRequesterPhone(request.getRequester().getPhone());
            dto.setRequesterDepartment(request.getRequester().getDepartment());
        }

        // Category & Type
        dto.setCategoryName(request.getCategory() != null ? request.getCategory().getName() : null);
        dto.setTypeName(request.getRequestType() != null ? request.getRequestType().getName() : null);

        // Assignment info
        if (request.getDepartment() != null) {
            dto.setDepartmentId(request.getDepartment().getId());
            dto.setDepartmentName(request.getDepartment().getName());
        }
        if (request.getAssignedAgent() != null) {
            dto.setAssignedAgentId(request.getAssignedAgent().getId());
            dto.setAssignedAgentName(request.getAssignedAgent().getUsername());
            dto.setAssignedAgentEmail(request.getAssignedAgent().getEmail());
        }

        // Activity log
        List<ActivityLog> activities = activityLogRepository.findByRequestIdOrderByCreatedAtDesc(requestId);
        List<ActivityLogDTO> activityDTOs = activities.stream()
                .map(this::convertActivityToDTO)
                .collect(Collectors.toList());
        dto.setActivityLog(activityDTOs);

        return dto;
    }

    /**
     * Log activity for audit trail
     */
    private void logActivity(ServiceRequest request, String actionType, String oldValue, String newValue,
            String notes) {
        ActivityLog log = new ActivityLog();
        log.setRequest(request);
        log.setActionType(actionType);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setNotes(notes);
        // TODO: Set performedBy from SecurityContext
        activityLogRepository.save(log);
    }

    /**
     * Convert ActivityLog to DTO
     */
    private ActivityLogDTO convertActivityToDTO(ActivityLog log) {
        ActivityLogDTO dto = new ActivityLogDTO();
        dto.setId(log.getId());
        dto.setActionType(log.getActionType());
        dto.setPerformedBy(log.getPerformedBy() != null ? log.getPerformedBy().getUsername() : "System");
        dto.setOldValue(log.getOldValue());
        dto.setNewValue(log.getNewValue());
        dto.setNotes(log.getNotes());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }

    /**
     * Delete request (Admin only)
     * Permanently removes a service request and all related data
     */
    public void deleteRequest(Long requestId) {
        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with ID: " + requestId));

        String ticketId = request.getTicketId();

        // Delete request (cascades to related entities like comments, attachments,
        // status history)
        requestRepository.delete(request);

        System.out.println("✓ Request " + ticketId + " deleted successfully");
    }
}
