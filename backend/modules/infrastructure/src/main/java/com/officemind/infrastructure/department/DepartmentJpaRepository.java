package com.officemind.infrastructure.department;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface DepartmentJpaRepository extends JpaRepository<DepartmentJpaEntity, UUID> {

    boolean existsByName(String name);

    Page<DepartmentJpaEntity> findAll(Pageable pageable);
}
