package com.officemind.api.department;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(
        @NotBlank(message = "name is required") String name,
        String description
) {
}
