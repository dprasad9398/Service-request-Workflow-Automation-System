package com.servicedesk.service;

import com.servicedesk.entity.User;
import com.servicedesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * User Service
 * Business logic for user operations
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get current user profile
     */
    public User getCurrentUserProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * Update current user profile
     */
    @Transactional
    public User updateProfile(String username, User updatedProfile) {
        User user = getCurrentUserProfile(username);

        user.setFirstName(updatedProfile.getFirstName());
        user.setLastName(updatedProfile.getLastName());
        user.setPhone(updatedProfile.getPhone());
        user.setDepartment(updatedProfile.getDepartment());

        // Only update password if provided
        if (updatedProfile.getPassword() != null && !updatedProfile.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedProfile.getPassword()));
        }

        return userRepository.save(user);
    }

    /**
     * Get user dashboard statistics
     */
    public Map<String, Object> getUserDashboardStats(String username) {
        Map<String, Object> stats = new HashMap<>();

        User user = getCurrentUserProfile(username);

        // Add user-specific stats here
        stats.put("userId", user.getId());
        stats.put("username", user.getUsername());
        stats.put("fullName", user.getFullName());
        stats.put("email", user.getEmail());
        stats.put("department", user.getDepartment());

        // Placeholder for request statistics
        // These will be populated when request service is integrated
        stats.put("totalRequests", 0);
        stats.put("pendingRequests", 0);
        stats.put("completedRequests", 0);
        stats.put("cancelledRequests", 0);

        return stats;
    }

    /**
     * Change password
     */
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = getCurrentUserProfile(username);

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update to new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
