package com.zorvyn.finance.controller;

import com.zorvyn.finance.dto.ApiResponse;
import com.zorvyn.finance.dto.UserRoleRequest;
import com.zorvyn.finance.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Get all users — Admin only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(
                ApiResponse.ok("Users fetched successfully",
                        userService.getAllUsers()));
    }

    // Get single user — Admin only
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok("User fetched successfully",
                        userService.getUserById(id)));
    }

    // Update role — Admin only
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRole(@PathVariable Long id,
                                        @Valid @RequestBody UserRoleRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Role updated successfully",
                        userService.updateRole(id, request.getRole())));
    }

    // Activate or deactivate user — Admin only
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam boolean active) {
        return ResponseEntity.ok(
                ApiResponse.ok("User status updated successfully",
                        userService.updateStatus(id, active)));
    }

    // Delete user — Admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id,
                                        @AuthenticationPrincipal String currentUserEmail) {
        userService.deleteUser(id, currentUserEmail);
        return ResponseEntity.ok(ApiResponse.ok("User deleted successfully"));
    }
}