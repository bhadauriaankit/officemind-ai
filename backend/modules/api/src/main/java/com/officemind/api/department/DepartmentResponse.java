package com.officemind.api.department;

import com.officemind.domain.department.Department;

import java.time.Instant;
import java.util.UUID;

public record DepartmentResponse(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
    public static DepartmentResponse from(Department department) {
        return new DepartmentResponse(
                department.getId().value(),
                department.getName(),
                department.getDescription(),
                department.getCreatedAt(),
                department.getUpdatedAt()
        );
    }
}
