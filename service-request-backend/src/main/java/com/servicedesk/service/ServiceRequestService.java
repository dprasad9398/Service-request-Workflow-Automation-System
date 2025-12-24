package com.servicedesk.service;

import com.servicedesk.dto.ServiceRequestDTO;
import com.servicedesk.entity.*;
import com.servicedesk.exception.ResourceNotFoundException;
import com.servicedesk.repository.*;
import com.servicedesk.util.TicketIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service Request Service
 * Business logic for service request management
 */
@Service
@Transactional
public class ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    /**
     * Get my service requests
     */
    public Page<ServiceRequest> getMyServiceRequests(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return serviceRequestRepository.findByRequesterId(user.getId(), pageable);
    }

    @Autowired
    private ServiceCatalogRepository serviceCatalogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SLATrackingRepository slaTrackingRepository;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;

    /**
     * Create new service request
     */
    public ServiceRequest createServiceRequest(ServiceRequestDTO requestDTO, String username) {
        // Get requester
        User requester = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Get service
        ServiceCatalog service = serviceCatalogRepository.findById(requestDTO.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", requestDTO.getServiceId()));

        // Create service request
        ServiceRequest request = new ServiceRequest();
        request.setTicketId(TicketIdGenerator.generateTicketId());
        request.setService(service);
        request.setRequester(requester);
        request.setTitle(requestDTO.getTitle());
        request.setDescription(requestDTO.getDescription());
        request.setPriority(requestDTO.getPriority());

        // Set initial status based on approval requirement
        if (service.getRequiresApproval()) {
            request.setStatus(ServiceRequest.RequestStatus.PENDING_APPROVAL);
        } else {
            request.setStatus(ServiceRequest.RequestStatus.NEW);
        }

        ServiceRequest savedRequest = serviceRequestRepository.save(request);

        // Create SLA tracking if SLA is defined
        if (service.getSla() != null) {
            createSLATracking(savedRequest, service.getSla());
        }

        // Trigger workflow if exists
        triggerWorkflow(savedRequest);

        return savedRequest;
    }

    /**
     * Get service request by ID
     */
    public ServiceRequest getServiceRequestById(Long id) {
        return serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceRequest", "id", id));
    }

    /**
     * Get service request by ticket ID
     */
    public ServiceRequest getServiceRequestByTicketId(String ticketId) {
        return serviceRequestRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceRequest", "ticketId", ticketId));
    }

    /**
     * Get all service requests with pagination
     */
    public Page<ServiceRequest> getAllServiceRequests(Pageable pageable) {
        return serviceRequestRepository.findAll(pageable);
    }

    /**
     * Get service requests by requester
     */
    public Page<ServiceRequest> getServiceRequestsByRequester(Long requesterId, Pageable pageable) {
        return serviceRequestRepository.findByRequesterId(requesterId, pageable);
    }

    /**
     * Get service requests by assigned agent
     */
    public Page<ServiceRequest> getServiceRequestsByAssignedTo(Long agentId, Pageable pageable) {
        return serviceRequestRepository.findByAssignedToId(agentId, pageable);
    }

    /**
     * Get service requests by status
     */
    public Page<ServiceRequest> getServiceRequestsByStatus(ServiceRequest.RequestStatus status, Pageable pageable) {
        return serviceRequestRepository.findByStatus(status, pageable);
    }

    /**
     * Update service request status
     */
    public ServiceRequest updateServiceRequestStatus(Long id, ServiceRequest.RequestStatus newStatus) {
        ServiceRequest request = getServiceRequestById(id);
        request.setStatus(newStatus);

        if (newStatus == ServiceRequest.RequestStatus.CLOSED) {
            request.setClosedAt(LocalDateTime.now());

            // Update SLA tracking
            SLATracking slaTracking = slaTrackingRepository.findByRequestId(id).orElse(null);
            if (slaTracking != null) {
                slaTracking.setResolutionCompletedAt(LocalDateTime.now());
                slaTrackingRepository.save(slaTracking);
            }
        }

        return serviceRequestRepository.save(request);
    }

    /**
     * Assign service request to agent
     */
    public ServiceRequest assignServiceRequest(Long requestId, Long agentId) {
        ServiceRequest request = getServiceRequestById(requestId);
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", agentId));

        request.setAssignedTo(agent);
        request.setStatus(ServiceRequest.RequestStatus.ASSIGNED);

        return serviceRequestRepository.save(request);
    }

    /**
     * Cancel service request
     */
    public ServiceRequest cancelServiceRequest(Long id) {
        ServiceRequest request = getServiceRequestById(id);
        request.setStatus(ServiceRequest.RequestStatus.CANCELLED);
        request.setClosedAt(LocalDateTime.now());
        return serviceRequestRepository.save(request);
    }

    /**
     * Add resolution notes
     */
    public ServiceRequest addResolutionNotes(Long id, String notes) {
        ServiceRequest request = getServiceRequestById(id);
        request.setResolutionNotes(notes);
        request.setStatus(ServiceRequest.RequestStatus.RESOLVED);
        return serviceRequestRepository.save(request);
    }

    /**
     * Create SLA tracking for request
     */
    private void createSLATracking(ServiceRequest request, SLA sla) {
        SLATracking tracking = new SLATracking();
        tracking.setRequest(request);
        tracking.setSla(sla);

        LocalDateTime now = LocalDateTime.now();
        tracking.setResponseDueAt(now.plusHours(sla.getResponseTimeHours()));
        tracking.setResolutionDueAt(now.plusHours(sla.getResolutionTimeHours()));

        slaTrackingRepository.save(tracking);
    }

    /**
     * Trigger workflow for request
     */
    private void triggerWorkflow(ServiceRequest request) {
        workflowRepository.findByServiceIdAndIsActive(request.getService().getId(), true)
                .ifPresent(workflow -> {
                    WorkflowInstance instance = new WorkflowInstance();
                    instance.setRequest(request);
                    instance.setWorkflow(workflow);
                    instance.setStatus(WorkflowInstance.InstanceStatus.PENDING);
                    workflowInstanceRepository.save(instance);
                });
    }
}
