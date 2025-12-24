package com.servicedesk.controller;

import com.servicedesk.dto.ApiResponse;
import com.servicedesk.entity.User;
import com.servicedesk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * User Controller
 * Handles user-specific operations
 * All endpoints require ROLE_USER or ROLE_ADMIN
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get current user profile
     * GET /api/user/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_END_USER', 'ROLE_ADMIN')")
    public ResponseEntity<User> getCurrentUserProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getCurrentUserProfile(username);
        return ResponseEntity.ok(user);
    }

    /**
     * Update current user profile
     * PUT /api/user/profile
     */
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_END_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody User updatedProfile) {
        try {
            String username = authentication.getName();
            User user = userService.updateProfile(username, updatedProfile);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to update profile: " + e.getMessage()));
        }
    }

    /**
     * Get user dashboard statistics
     * GET /api/user/dashboard/stats
     */
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_END_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserDashboardStats(Authentication authentication) {
        String username = authentication.getName();
        Map<String, Object> stats = userService.getUserDashboardStats(username);
        return ResponseEntity.ok(stats);
    }

    /**
     * Change password
     * POST /api/user/change-password
     */
    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_END_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> changePassword(Authentication authentication, @RequestBody Map<String, String> request) {
        try {
            String username = authentication.getName();
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            userService.changePassword(username, oldPassword, newPassword);
            return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to change password: " + e.getMessage()));
        }
    }
}
