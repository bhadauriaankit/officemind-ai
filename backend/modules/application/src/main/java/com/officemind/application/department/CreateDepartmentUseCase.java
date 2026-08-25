package com.officemind.application.department;

import com.officemind.common.exception.ConflictException;
import com.officemind.domain.department.Department;
import org.springframework.stereotype.Service;

@Service
public class CreateDepartmentUseCase {

    private final DepartmentRepositoryPort departmentRepository;

    public CreateDepartmentUseCase(DepartmentRepositoryPort departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department execute(String name, String description) {
        if (departmentRepository.existsByName(name)) {
            throw new ConflictException("A department named '" + name + "' already exists");
        }
        Department department = Department.create(name, description);
        return departmentRepository.save(department);
    }
}
