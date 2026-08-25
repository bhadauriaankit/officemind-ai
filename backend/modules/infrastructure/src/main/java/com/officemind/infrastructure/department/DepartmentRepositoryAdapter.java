package com.officemind.infrastructure.department;

import com.officemind.application.department.DepartmentRepositoryPort;
import com.officemind.common.paging.PageResult;
import com.officemind.domain.department.Department;
import com.officemind.domain.shared.EntityId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DepartmentRepositoryAdapter implements DepartmentRepositoryPort {

    private final DepartmentJpaRepository jpaRepository;

    public DepartmentRepositoryAdapter(DepartmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Department save(Department department) {
        DepartmentJpaEntity saved = jpaRepository.save(DepartmentMapper.toJpa(department));
        return DepartmentMapper.toDomain(saved);
    }

    @Override
    public Optional<Department> findById(EntityId id) {
        return jpaRepository.findById(id.value()).map(DepartmentMapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public PageResult<Department> findAll(int page, int size) {
        Page<DepartmentJpaEntity> result = jpaRepository.findAll(PageRequest.of(page, size));
        return new PageResult<>(
                result.getContent().stream().map(DepartmentMapper::toDomain).toList(),
                page,
                size,
                result.getTotalElements()
        );
    }

    @Override
    public void deleteById(EntityId id) {
        jpaRepository.deleteById(id.value());
    }
}
