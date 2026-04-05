package com.zorvyn.finance.dto;

import com.zorvyn.finance.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRoleRequest {

    @NotNull(message = "Role is required")
    private Role role;
}