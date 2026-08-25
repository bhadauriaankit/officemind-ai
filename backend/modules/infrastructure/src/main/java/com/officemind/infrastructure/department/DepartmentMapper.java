package com.officemind.infrastructure.department;

import com.officemind.domain.department.Department;
import com.officemind.domain.shared.EntityId;

final class DepartmentMapper {

    private DepartmentMapper() {
    }

    static Department toDomain(DepartmentJpaEntity entity) {
        return Department.rehydrate(
                EntityId.of(entity.getId()),
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    static DepartmentJpaEntity toJpa(Department department) {
        return new DepartmentJpaEntity(
                department.getId().value(),
                department.getName(),
                department.getDescription(),
                department.getCreatedAt(),
                department.getUpdatedAt()
        );
    }
}
