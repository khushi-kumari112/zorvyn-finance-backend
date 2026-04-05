package com.zorvyn.finance.service;

import com.zorvyn.finance.model.Role;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get single user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + id));
    }

    // Update user role — Admin only
    public User updateRole(Long id, Role role) {
        User user = getUserById(id);
        user.setRole(role);
        return userRepository.save(user);
    }

    // Activate or deactivate a user — Admin only
    public User updateStatus(Long id, boolean active) {
        User user = getUserById(id);
        user.setActive(active);
        return userRepository.save(user);
    }

    // Delete user permanently — Admin only
    public void deleteUser(Long id, String currentUserEmail) {
        User user = getUserById(id);
        if (user.getEmail().equals(currentUserEmail)) {
            throw new RuntimeException(
                    "You cannot delete your own account");
        }
        userRepository.delete(user);
    }
}