package com.servicedesk.controller;

import com.servicedesk.entity.User;
import com.servicedesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Debug Controller - FOR TESTING ONLY
 */
@RestController
@RequestMapping("/debug")
@CrossOrigin(origins = "*")
public class DebugController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/reset-admin-password")
    public ResponseEntity<?> resetAdminPassword(@RequestBody Map<String, String> request) {
        String newPassword = request.get("password");

        Map<String, Object> response = new HashMap<>();

        User user = userRepository.findByUsername("admin").orElse(null);

        if (user == null) {
            response.put("success", false);
            response.put("message", "Admin user not found");
            return ResponseEntity.ok(response);
        }

        // Encode the new password
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "Password updated successfully");
        response.put("newHashStart", encodedPassword.substring(0, 20));

        // Test if it matches
        boolean matches = passwordEncoder.matches(newPassword, encodedPassword);
        response.put("testMatches", matches);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-user/{username}")
    public ResponseEntity<?> checkUser(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            response.put("found", false);
            response.put("message", "User not found");
            return ResponseEntity.ok(response);
        }

        response.put("found", true);
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("isActive", user.getIsActive());
        response.put("rolesCount", user.getRoles().size());
        response.put("roles", user.getRoles().stream().map(r -> r.getName()).toList());
        response.put("passwordHashStart", user.getPassword().substring(0, 20));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/test-password")
    public ResponseEntity<?> testPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Map<String, Object> response = new HashMap<>();

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            response.put("userFound", false);
            return ResponseEntity.ok(response);
        }

        response.put("userFound", true);
        response.put("passwordMatches", passwordEncoder.matches(password, user.getPassword()));
        response.put("isActive", user.getIsActive());
        response.put("hasRoles", !user.getRoles().isEmpty());

        return ResponseEntity.ok(response);
    }
}
